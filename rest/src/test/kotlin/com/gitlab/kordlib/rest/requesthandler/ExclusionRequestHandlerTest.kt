package com.gitlab.kordlib.rest.requesthandler

import com.gitlab.kordlib.rest.ratelimit.ExclusionRequestHandler
import com.gitlab.kordlib.rest.request.RequestBuilder
import com.gitlab.kordlib.rest.route.Route
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpStatusCode
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.asserter
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
@ExperimentalCoroutinesApi
class ExclusionRequestHandlerTest {

    val timeout = 1000.seconds
    val instant = Instant.EPOCH
    val clock = Clock.fixed(instant, ZoneOffset.UTC)
    var firstRequest = atomic(true)

    @BeforeTest
    fun setup() {
        firstRequest.update { true }
    }

    @Test
    fun `an ExclusionRequestHandler sending a request that results in a route timeout will wait for that timeout on the next call`() = runBlockingTest {
        val client = httpClient {
            this["X-RateLimit-Remaining"] = "0"
            this["X-RateLimit-Reset"] = "${clock.instant().epochSecond + timeout.inSeconds.toLong()}"
        }

        val handler = ExclusionRequestHandler(client, clock)

        handler.handle(request) //get first timeout
        handler.handle(request)

        asserter.assertTrue("current time should be ${timeout.toLongMilliseconds()} but was $currentTime", currentTime == timeout.toLongMilliseconds())

        client.close()
    }

    @Test
    fun `an ExclusionRequestHandler sending a request that results in a global timeout will wait for that timeout on the next call`() = runBlockingTest {
        val client = httpClient {
            this["X-RateLimit-Global"] = "true"
            this["Retry-After"] = "${timeout.toLongMilliseconds()}"
        }

        val handler = ExclusionRequestHandler(client, clock)

        handler.handle(request)
        handler.handle(request)

        asserter.assertTrue("current time should be ${timeout.toLongMilliseconds()} but was $currentTime", currentTime == timeout.toLongMilliseconds())

        client.close()
    }

    private fun httpClient(
            code: HttpStatusCode = HttpStatusCode.TooManyRequests,
            block: HeadersBuilder.() -> Unit) = HttpClient(MockEngine) {
        engine {
            addHandler {
                if (firstRequest.getAndSet(false)) {
                    respond("", code, HeadersBuilder().apply(block).build())
                } else {
                    respond("", HttpStatusCode.Accepted)
                }
            }
        }
    }

    @Test
    fun `an ExclusionRequestHandler sending a request that results in a bucket timeout will wait for that timeout on a similar bucket call`() = runBlockingTest {
        val count = atomic(0)

        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    when (count.getAndIncrement()) {
                        0, 1 -> respond("", HttpStatusCode.Accepted, Headers.build {
                            // discovery
                            this["X-RateLimit-Remaining"] = "1"
                            this["X-RateLimit-Bucket"] = "abc"
                        })
                        2 -> respond("", HttpStatusCode.TooManyRequests, Headers.build {
                            // rate limit discovery
                            this["X-RateLimit-Remaining"] = "0"
                            this["X-RateLimit-Reset"] = "${clock.instant().epochSecond + timeout.inSeconds.toLong()}"
                            this["X-RateLimit-Bucket"] = "abc"
                        })
                        3 -> respond("", HttpStatusCode.Accepted, Headers.build {
                            // retry doesn't limit anymore
                            this["X-RateLimit-Remaining"] = "1"
                            this["X-RateLimit-Bucket"] = "abc"
                        })
                        else -> respond("", HttpStatusCode.Accepted, Headers.build {
                            this["X-RateLimit-Remaining"] = "0"
                            this["X-RateLimit-Reset"] = "${clock.instant().epochSecond + timeout.inSeconds.toLong()}"
                            this["X-RateLimit-Bucket"] = "abc"
                        })
                    }

                }
            }
        }
        val handler = ExclusionRequestHandler(client, clock)



        handler.handle(request)
        handler.handle(request2) //explore bucket
        handler.handle(request)
        handler.handle(request2)

        asserter.assertTrue("current time should be ${timeout.toLongMilliseconds()} but was $currentTime",
                currentTime / 2 /*since time doesn't pass we're waiting for both the route and the bucket timeout*/ == timeout.toLongMilliseconds())

        client.close()
    }

    val request = with(RequestBuilder(Route.PinDelete)) {
        keys[Route.ChannelId] = "420"
        keys[Route.MessageId] = "1337"
        build()
    }

    val request2 = with(RequestBuilder(Route.AllReactionsDelete)) {
        keys[Route.ChannelId] = "420"
        keys[Route.MessageId] = "1337"
        build()
    }

}
