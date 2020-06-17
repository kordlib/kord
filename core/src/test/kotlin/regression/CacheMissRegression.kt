package regression

import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.gateway.Command
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.GatewayConfiguration
import com.gitlab.kordlib.rest.request.JsonRequest
import com.gitlab.kordlib.rest.request.MultipartRequest
import com.gitlab.kordlib.rest.request.Request
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Route
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.request
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.readText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.takeFrom
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.BeforeTest
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


private val parser = Json(JsonConfiguration(encodeDefaults = false, allowStructuredMapKeys = true, ignoreUnknownKeys = true, isLenient = true))

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
            headers.append("X-RateLimit-Precision", "millisecond")
            headers.appendAll(request.headers)

            url {
                url.takeFrom(Route.baseUrl)
                encodedPath += request.path
                parameters.appendAll(request.parameters)
            }


            request.body?.let {
                when (request) {
                    is MultipartRequest<*, *> -> {
                        headers.append("payload_json", parser.stringify(it.strategy as SerializationStrategy<Any>, it.body))
                        this.body = MultiPartFormDataContent(request.data)
                    }

                    is JsonRequest<*, *> -> {
                        val json = parser.stringify(it.strategy as SerializationStrategy<Any>, it.body)
                        this.body = TextContent(json, ContentType.Application.Json)
                    }
                }
            }


        }.execute()

        return parser.parse(request.route.strategy, response.readText())


    }
}

class CacheMissingRegressions {
    lateinit var kord: Kord

    @BeforeTest
    fun setup() {
        runBlocking {
            kord = Kord(System.getenv("KORD_TEST_TOKEN")) {
                gateways { _, shards -> shards.map { FakeGateway } }
                requestHandler { CrashingHandler(it.httpClient) }
            }
        }
    }


    @Test
    @EnabledIfEnvironmentVariable(named = "TARGET_BRANCH", matches = "master")
    fun `if data not in cache explode`() {
        val id = 5L
        assertThrows<IllegalStateException> {
            runBlocking {
                kord.getChannel(Snowflake(id))
            }
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "TARGET_BRANCH", matches = "master")
    fun `if data in cache don't fetch from rest`() {
        runBlocking {
            val id = 5L
            kord.cache.put(ChannelData(id, ChannelType.GuildText))

            kord.getChannel(Snowflake(id))
        }
    }

}