package com.gitlab.kordlib.rest.requesthandler

import com.gitlab.kordlib.rest.json.request.MessageCreateRequest
import com.gitlab.kordlib.rest.ratelimit.ExclusionRequestHandler
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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

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

    val parallelRequestHandler = ParallelRequestHandler(client)
    val exclusionRequestHandler = ExclusionRequestHandler(client)
    val parallelRest = RestClient(parallelRequestHandler)
    val exclusionRest = RestClient(exclusionRequestHandler)


    @Test
    @Disabled
    @ExperimentalTime
    fun `parallelism`() = runBlocking {
        val time = measureTime {
            val first = launch(Dispatchers.IO) { parallelRest.channel.getMessages(channel) }
            val second = launch(Dispatchers.IO) { parallelRest.channel.createMessage(channel, MessageCreateRequest("parallel test")) }
            joinAll(first, second)
        }

        println("parallel took ${time.inMilliseconds} ms")
    }

    @Test
    @Disabled
    @ExperimentalTime
    fun `serial`() = runBlocking {
        val time = measureTime {
            val first = launch(Dispatchers.IO) { exclusionRest.channel.getMessages(channel) }
            val second = launch(Dispatchers.IO) { exclusionRest.channel.createMessage(channel, MessageCreateRequest("serial test")) }
            joinAll(first, second)
        }

        println("serial took ${time.inMilliseconds} ms")
    }
}