package com.gitlab.kordlib.rest.requesthandler

import com.gitlab.kordlib.rest.json.request.MessageCreateRequest
import com.gitlab.kordlib.rest.ratelimit.ParallelRequestHandler
import com.gitlab.kordlib.rest.service.RestClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis
import kotlin.test.asserter

@KtorExperimentalAPI
class RequestHandlerBenchmark {
    val token = System.getenv("token")
    val channel = System.getenv("testChannel")


    val client = HttpClient(CIO) {
        install(WebSockets)
        install(JsonFeature)
        defaultRequest {
            header("Authorization", "Bot $token")
        }
    }

    val handler = ParallelRequestHandler(client)
    val rest = RestClient(handler)


    @Test
    fun `parallelism`() = runBlocking {
        val time1 = measureTimeMillis {
            val first = launch(Dispatchers.IO) { rest.channel.getMessages(channel) }
            val second = launch(Dispatchers.IO) { rest.channel.getMessages(channel) }
            joinAll(first, second)
        }

        val time2 = measureTimeMillis {
            val first = launch(Dispatchers.IO) { rest.channel.getMessages(channel) }
            val second = launch(Dispatchers.IO) { rest.channel.createMessage(channel, MessageCreateRequest("TEST")) }
            joinAll(first, second)
        }
        asserter.assertTrue("time2 must be less than time1 but was $time2", time2 < time1)
    }
}