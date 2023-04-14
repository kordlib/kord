package dev.kord.core.regression

import dev.kord.cache.api.put
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.withKord
import dev.kord.gateway.Command
import dev.kord.gateway.Event
import dev.kord.gateway.Gateway
import dev.kord.gateway.GatewayConfiguration
import dev.kord.rest.request.JsonRequest
import dev.kord.rest.request.MultipartRequest
import dev.kord.rest.request.Request
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Duration


private val parser = Json {
    encodeDefaults = false
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
    isLenient = true
}

object FakeGateway : Gateway {

    private val deferred = CompletableDeferred<Unit>()

    override val events: SharedFlow<Event> = MutableSharedFlow()

    override val ping: StateFlow<Duration?> = MutableStateFlow(null)

    override suspend fun detach() {}

    override suspend fun send(command: Command) {}
    override suspend fun start(configuration: GatewayConfiguration) {
        deferred.await()
    }

    override suspend fun stop() {
        deferred.complete(Unit)
    }

    override val coroutineContext: CoroutineContext = SupervisorJob() + EmptyCoroutineContext
}

class CrashingHandler(private val client: HttpClient, override val token: String) : RequestHandler {
    override suspend fun <B : Any, R> handle(request: Request<B, R>): R {
        if (request.route != Route.CurrentUserGet) throw IllegalStateException("shouldn't do a request")
        val response = client.request {
            method = request.route.method
            headers.appendAll(request.headers)

            url {
                url.takeFrom(request.baseUrl)
                encodedPath += request.path
                parameters.appendAll(request.parameters)
            }


            request.body?.let {
                @Suppress("UNCHECKED_CAST")
                when (request) {
                    is MultipartRequest<*, *> -> {
                        headers.append(
                            "payload_json",
                            parser.encodeToString(it.strategy as SerializationStrategy<Any>, it.body)
                        )
                        setBody(MultiPartFormDataContent(request.data))
                    }

                    is JsonRequest<*, *> -> {
                        val json = parser.encodeToString(it.strategy as SerializationStrategy<Any>, it.body)
                        setBody(TextContent(json, ContentType.Application.Json))
                    }
                }
            }
        }

        return request.route.mapper.deserialize(parser, response.bodyAsText())
    }
}

class CacheMissingRegressions {

    @Test
    @JsName("test1")
    fun `if data not in cache explode`() = runTest {
        withKord { kord ->
            val id = 5uL
            assertFailsWith<IllegalStateException> { kord.getChannel(Snowflake(id)) }
        }
    }

    @Test
    @JsName("test2")
    fun `if data in cache don't fetch from rest`() = runTest {
        withKord { kord ->
            val id = Snowflake(5uL)
            kord.cache.put(ChannelData(id, ChannelType.GuildText))

            kord.getChannel(id)
        }
    }
}
