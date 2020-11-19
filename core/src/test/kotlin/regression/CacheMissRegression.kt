package regression

import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.cache.map.MapDataCache
import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.ClientResources
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.builder.kord.configure
import com.gitlab.kordlib.core.builder.kord.getBotIdFromToken
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.cache.registerKordData
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.gateway.*
import com.gitlab.kordlib.rest.request.JsonRequest
import com.gitlab.kordlib.rest.request.MultipartRequest
import com.gitlab.kordlib.rest.request.Request
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Route
import com.gitlab.kordlib.rest.service.RestClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
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

    override val events: Flow<Event>
        get() = emptyFlow()

    @ExperimentalTime
    override val ping: Duration
        get() = Duration.ZERO

    override suspend fun detach() {}

    override suspend fun send(command: Command) {}
    override suspend fun start(configuration: GatewayConfiguration) {
        deferred.await()
    }

    override suspend fun stop() {
        deferred.complete(Unit)
    }
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
                        headers.append("payload_json", parser.encodeToString(it.strategy as SerializationStrategy<Any>, it.body))
                        this.body = MultiPartFormDataContent(request.data)
                    }

                    is JsonRequest<*, *> -> {
                        val json = parser.encodeToString(it.strategy as SerializationStrategy<Any>, it.body)
                        this.body = TextContent(json, ContentType.Application.Json)
                    }
                }
            }


        }.execute()

        return parser.decodeFromString(request.route.strategy, response.readText())


    }
}

@EnabledIfEnvironmentVariable(named = "TARGET_BRANCH", matches = "master")
class CacheMissingRegressions {
    lateinit var kord: Kord

    @BeforeTest
    fun setup() = runBlockingTest { //TODO, move this over to entity supplier tests instead, eventually.
        val token = System.getenv("KORD_TEST_TOKEN")
        val resources = ClientResources(token, 1, null.configure(token), EntitySupplyStrategy.cacheWithRestFallback, Intents.nonPrivileged)
        kord = Kord(
                resources,
                MapDataCache().also { it.registerKordData() },
                MasterGateway(mapOf(0 to FakeGateway)),
                RestClient(CrashingHandler(resources.httpClient)),
                getBotIdFromToken(token),
                BroadcastChannel(1),
                Dispatchers.Default
        )
    }


    @Test
    fun `if data not in cache explode`() {
        val id = 5L
        assertThrows<IllegalStateException> {
            runBlocking {
                kord.getChannel(Snowflake(id))
            }
        }
    }

    @Test
    fun `if data in cache don't fetch from rest`() {
        runBlocking {
            val id = Snowflake(5L)
            kord.cache.put(ChannelData(id, ChannelType.GuildText))

            kord.getChannel(id)
        }
    }

}