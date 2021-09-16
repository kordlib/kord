package regression

import dev.kord.cache.api.put
import dev.kord.cache.map.MapDataCache
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.builder.kord.Shards
import dev.kord.core.builder.kord.configure
import dev.kord.core.builder.kord.getBotIdFromToken
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.registerKordData
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.*
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.BeforeTest
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


private val parser = Json {
    encodeDefaults = false
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
    isLenient = true
}

object FakeGateway : Gateway {

    val deferred = CompletableDeferred<Unit>()

    override val events: SharedFlow<Event> = MutableSharedFlow<Event>()

    @ExperimentalTime
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

class CrashingHandler(val client: HttpClient) : RequestHandler {
    override suspend fun <B : Any, R> handle(request: Request<B, R>): R {
        if (request.route != Route.CurrentUserGet) throw IllegalStateException("shouldn't do a request")
        val response = client.request<HttpStatement> {
            method = request.route.method
            headers.appendAll(request.headers)

            url {
                url.takeFrom(Route.baseUrl)
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
                        this.body = MultiPartFormDataContent(request.data)
                    }

                    is JsonRequest<*, *> -> {
                        val json = parser.encodeToString(it.strategy as SerializationStrategy<Any>, it.body)
                        this.body = TextContent(json, ContentType.Application.Json)
                    }
                }
            }


        }.execute()

        return request.route.mapper.deserialize(parser, response.readText())
    }
}

@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
class CacheMissingRegressions {
    lateinit var kord: Kord

    @BeforeTest
    fun setup() = runBlockingTest { //TODO, move this over to entity supplier tests instead, eventually.
        val token = System.getenv("KORD_TEST_TOKEN")
        val resources = ClientResources(
            token,
            getBotIdFromToken(token),
            Shards(1),
            null.configure(token),
            EntitySupplyStrategy.cacheWithRestFallback,
            Intents.nonPrivileged
        )
        kord = Kord(
            resources,
            MapDataCache().also { it.registerKordData() },
            DefaultMasterGateway(mapOf(0 to FakeGateway)),
            RestClient(CrashingHandler(resources.httpClient)),
            getBotIdFromToken(token),
            MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE),
            Dispatchers.Default,
            null
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
