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
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.request.Request
import com.gitlab.kordlib.rest.route.Route
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.http.takeFrom
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Exception
import java.lang.IllegalStateException
import kotlin.test.BeforeTest
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

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
    override suspend fun <T> handle(request: Request<T>): HttpResponse {
        if (request.route == Route.CurrentUserGet) {
            val builder = HttpRequestBuilder().apply {
                headers.append("X-RateLimit-Precision", "millisecond")
                url.takeFrom(Route.baseUrl)
                with(request) { apply() }
            }

            return client.request<HttpStatement>(builder).execute()
        }
        throw IllegalStateException("shouldn't do a request")
    }
}

class CacheMissingRegressions {
    lateinit var kord: Kord

    @BeforeTest
    fun setup() {
        runBlocking {
            kord = Kord(System.getenv("token")) {
                gateways { _, shards -> shards.map { FakeGateway } }
                requestHandler { CrashingHandler(it.httpClient) }
            }
        }
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
            val id = 5L
            kord.cache.put(ChannelData(id, ChannelType.GuildText))

            kord.getChannel(Snowflake(id))
        }
    }

}