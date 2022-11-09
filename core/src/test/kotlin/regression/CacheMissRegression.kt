package regression

import dev.kord.cache.api.put
import dev.kord.cache.map.MapDataCache
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.builder.kord.configure
import dev.kord.core.builder.kord.getBotIdFromToken
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.registerKordData
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.*
import dev.kord.gateway.builder.Shards
import dev.kord.rest.request.JsonRequest
import dev.kord.rest.request.MultipartRequest
import dev.kord.rest.request.Request
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import dev.kord.rest.service.RestClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.BeforeTest
import kotlin.time.Duration


private val parser = Json {
    encodeDefaults = false
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
    isLenient = true
}

object FakeGateway : Gateway {

    val deferred = CompletableDeferred<Unit>()

    override val events: SharedFlow<Event> = MutableSharedFlow<Event>()

    override val ping: StateFlow<Duration?> = MutableStateFlow(null)

    override suspend fun detach() {}

    override suspend fun send(command: Command) {}
    override suspend fun start(configuration: GatewayConfiguration) {
        deferred.await()
    }

    override suspend fun stop(closeReason: WebSocketCloseReason): GatewayResumeConfiguration {
        deferred.complete(Unit)
        error("Can't stop this!")
    }

    override suspend fun resume(configuration: GatewayResumeConfiguration) {
        deferred.await()
    }

    override val coroutineContext: CoroutineContext = SupervisorJob() + EmptyCoroutineContext
}

class CrashingHandler(val client: HttpClient, override val token: String) : RequestHandler {
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

@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
class CacheMissingRegressions {
    lateinit var kord: Kord

    @BeforeTest
    fun setup() = runTest { //TODO, move this over to entity supplier tests instead, eventually.
        val token = System.getenv("KORD_TEST_TOKEN")
        val resources = ClientResources(
            token,
            getBotIdFromToken(token),
            Shards(1),
            maxConcurrency = 1,
            null.configure(),
            EntitySupplyStrategy.cacheWithRestFallback,
        )
        kord = Kord(
            resources,
            MapDataCache().also { it.registerKordData() },
            DefaultMasterGateway(mapOf(0 to FakeGateway)),
            RestClient(CrashingHandler(resources.httpClient, resources.token)),
            getBotIdFromToken(token),
            MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE),
            Dispatchers.Default,
            DefaultGatewayEventInterceptor(),
        )
    }


    @Test
    fun `if data not in cache explode`() {
        val id = 5uL
        assertThrows<IllegalStateException> {
            runBlocking {
                kord.getChannel(Snowflake(id))
            }
        }
    }

    @Test
    fun `if data in cache don't fetch from rest`() {
        runBlocking {
            val id = Snowflake(5uL)
            kord.cache.put(ChannelData(id, ChannelType.GuildText))

            kord.getChannel(id)
        }
    }

}
