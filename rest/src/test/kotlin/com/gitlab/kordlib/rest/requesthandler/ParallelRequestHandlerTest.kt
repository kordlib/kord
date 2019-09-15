package com.gitlab.kordlib.rest.requesthandler

import com.gitlab.kordlib.rest.ratelimit.ParallelRequestHandler
import com.gitlab.kordlib.rest.request.RequestBuilder
import com.gitlab.kordlib.rest.route.Route
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
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
class ParallelRequestHandlerTest {

    val timeout = 1000.seconds
    val instant = Instant.EPOCH
    val clock = Clock.fixed(instant, ZoneOffset.UTC)
    var firstRequest = atomic(true)

    @BeforeTest
    fun setup(){
        firstRequest.update { true }
    }

    @Test
    fun `a ParallelRequestHandler sending a request that results in a route timeout will wait for that timeout on the next call`() = runBlockingTest {
        val client = httpClient {
            this["X-RateLimit-Remaining"] = "0"
            this["X-RateLimit-Reset"] = "${clock.instant().epochSecond + timeout.inSeconds.toLong()}"
        }

        val handler = ParallelRequestHandler(client, clock)

        handler.handle(request) //get first timeout
        handler.handle(request)

        asserter.assertTrue("current time should be ${timeout.toLongMilliseconds()} but was $currentTime", currentTime == timeout.toLongMilliseconds())

        client.close()
    }

    @Test
    fun `a ParallelRequestHandler sending a request that results in a global timeout will wait for that timeout on the next call`() = runBlockingTest {
        val client = httpClient {
            this["X-RateLimit-Global"] = "true"
            this["Retry-After"] = "${timeout.toLongMilliseconds()}"
        }

        val handler = ParallelRequestHandler(client, clock)

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

////disabled until 1.3.50
//object ExclusionRequestHandlerTest : Spek({
//
//    fun request() = with(RequestBuilder(Route.PinDelete)) {
//        keys[Route.ChannelId] = "420"
//        keys[Route.MessageId] = "1337"
//        build()
//    }
//
//    //seems to be some lazy loading being done, this gets everything initialized
//    fun warmupRequest() = with(RequestBuilder(Route.MessageDelete)) {
//        keys[Route.ChannelId] = "420"
//        keys[Route.MessageId] = "1337"
//        build()
//    }
//
//    xdescribe("an ExclusionRequestHandler") {
//
//        describe("sending a request that results in a route timeout") {
//            val timeout = 1000 / 1000
//
//            val client by memoized {
//                HttpClient(MockEngine) {
//                    engine {
//                        addHandler {
//                            val headers = with(HeadersBuilder()) {
//                                this["X-RateLimit-Remaining"] = "0"
//                                TODO() //this["X-RateLimit-Reset"] = "${(clock.nowMillis() + timeout) / (1000)}"
//
//                                build()
//                            }
//
//                            respond("", HttpStatusCode.Accepted, headers)
//                        }
//                    }
//                }
//            }
//
//            val requestHandler by memoized { ExclusionRequestHandler(client) }
//
//            it("will wait for that timeout on the next call") {
//                runBlocking {
//
//
//                    requestHandler.handle(warmupRequest())
//
//                    val time = measureTimeMillis {
//                        requestHandler.handle(request()) //get first timeout
//
//                        requestHandler.handle(request())
//                    }
//
//                    Assertions.assertTrue(time >= timeout) { "timeout expected to be greater to or equal than $timeout, but was $time" }
//                }
//
//
//            }
//
//            afterEach { client.close() }
//        }
//
//        xdescribe("sending a request that results in a global timeout") {
//            val timeout = 0 / 1000
//
//            val client by memoized {
//                HttpClient(MockEngine) {
//                    engine {
//                        addHandler {
//                            val headers = with(HeadersBuilder()) {
//                                this["X-RateLimit-Global"] = "true"
//                                this["Retry-After"] = "$timeout"
//
//                                build()
//                            }
//                            respond("", HttpStatusCode.Accepted, headers)
//                        }
//                    }
//                }
//            }
//
//            val requestHandler by memoized { ExclusionRequestHandler(client) }
//
//            it("will wait for that timeout on the next call") {
//                runBlocking {
//
//
//                    requestHandler.handle(warmupRequest())
//
//                    val time = measureTimeMillis {
//                        requestHandler.handle(request()) //get first timeout
//
//                        requestHandler.handle(request())
//                    }
//
//                    Assertions.assertTrue(time >= timeout) { "timeout expected to be greater to or equal than $timeout, but was $time" }
//                }
//
//            }
//
//            afterEach { client.close() }
//        }
//    }
//
//
//})
