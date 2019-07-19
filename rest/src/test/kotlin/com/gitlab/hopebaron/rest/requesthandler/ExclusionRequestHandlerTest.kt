package com.gitlab.hopebaron.rest.requesthandler

import com.gitlab.hopebaron.common.Platform
import com.gitlab.hopebaron.rest.ratelimit.ExclusionRequestHandler
import com.gitlab.hopebaron.rest.request.RequestBuilder
import com.gitlab.hopebaron.rest.route.Route
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.system.measureTimeMillis

object ExclusionRequestHandlerTest : Spek({

    fun request() = with(RequestBuilder(Route.PinDelete)) {
        keys[Route.ChannelId] = "420"
        keys[Route.MessageId] = "1337"
        build()
    }

    //seems to be some lazy loading being done, this gets everything initialized
    fun warmupRequest() = with(RequestBuilder(Route.MessageDelete)) {
        keys[Route.ChannelId] = "420"
        keys[Route.MessageId] = "1337"
        build()
    }

    describe("an ExclusionRequestHandler") {

        describe("sending a request that results in a route timeout") {
            val timeout = 100

            val client by memoized {
                HttpClient(MockEngine) {
                    engine {
                        addHandler {
                            val headers = with(HeadersBuilder()) {
                                this["X-RateLimit-Remaining"] = "0"
                                this["X-RateLimit-Reset"] = "${Platform.nowMillis() + timeout}"

                                build()
                            }

                            respond("", HttpStatusCode.Accepted, headers)
                        }
                    }
                }
            }

            val requestHandler by memoized { ExclusionRequestHandler(client) }

            it("will wait for that timeout on the next call") {
                runBlocking {


                    requestHandler.handle(warmupRequest())

                    val time = measureTimeMillis {
                        requestHandler.handle(request()) //get first timeout

                        requestHandler.handle(request())
                    }

                    Assertions.assertTrue(time >= timeout) { "timeout expected to be greater to or equal than $timeout, but was $time" }
                }


            }

            afterEach { client.close() }
        }

        describe("sending a request that results in a global timeout") {
            val timeout = 0

            val client by memoized {
                HttpClient(MockEngine) {
                    engine {
                        addHandler {
                            val headers = with(HeadersBuilder()) {
                                this["X-RateLimit-Global"] = "true"
                                this["Retry-After"] = "$timeout"

                                build()
                            }
                            respond("", HttpStatusCode.Accepted, headers)
                        }
                    }
                }
            }

            val requestHandler by memoized { ExclusionRequestHandler(client) }

            it("will wait for that timeout on the next call") {
                runBlocking {


                    requestHandler.handle(warmupRequest())

                    val time = measureTimeMillis {
                        requestHandler.handle(request()) //get first timeout

                        requestHandler.handle(request())
                    }

                    Assertions.assertTrue(time >= timeout) { "timeout expected to be greater to or equal than $timeout, but was $time" }
                }

            }

            afterEach { client.close() }
        }
    }


})