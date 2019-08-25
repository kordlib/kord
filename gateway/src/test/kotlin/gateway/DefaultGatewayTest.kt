package gateway

import com.gitlab.kordlib.common.entity.Activity
import com.gitlab.kordlib.common.entity.ActivityType
import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.common.ratelimit.BucketRateLimiter
import com.gitlab.kordlib.gateway.*
import com.gitlab.kordlib.gateway.retry.LinearRetry
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.UnstableDefault
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Duration

@FlowPreview
@UnstableDefault
@KtorExperimentalAPI
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class DefaultGatewayTest {
    @Test
    @Disabled
    fun `defualt gateway functions normally`() {
        val url = "wss://gateway.discord.gg/"
        val token = System.getenv("token")

        val client = HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature)
        }

        val retry = LinearRetry(2000, 10000, 10)
        val rateLimiter = BucketRateLimiter(120, Duration.ofSeconds(60))

        val gateway = DefaultGateway(url, client, retry, rateLimiter)

        GlobalScope.launch {
            gateway.events.filterIsInstance<MessageCreate>().flowOn(Dispatchers.IO).collect {
                val words = it.message.content.split(' ')
                when (words.firstOrNull()) {
                    "!close" -> gateway.close()
                    "!restart" -> gateway.restart()
                    "!status" -> when (words.getOrNull(1)) {
                        "playing" -> gateway.send(UpdateStatus(status = Status.Online, afk = false, game = Activity("Kord", ActivityType.Game)))
                    }
                }
            }
        }

        runBlocking {
            gateway.start(token)
        }
    }
}