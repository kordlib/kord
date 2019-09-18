package com.gitlab.kordlib.rest.requesthandler

import com.gitlab.kordlib.rest.ratelimit.ExclusionRequestHandler
import com.gitlab.kordlib.rest.request.RequestBuilder
import com.gitlab.kordlib.rest.route.Route
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.date.GMTDate
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runBlockingTest
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.BeforeTest
import kotlin.test.Ignore
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
    fun setup(){
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

    private fun httpClient(code: HttpStatusCode = HttpStatusCode.TooManyRequests, block: HeadersBuilder.() -> Unit) = HttpClient(MockEngine) {
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

    val request = with(RequestBuilder(Route.PinDelete)) {
        keys[Route.ChannelId] = "420"
        keys[Route.MessageId] = "1337"
        build()
    }

}
