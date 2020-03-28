package gateway

import com.gitlab.kordlib.common.entity.DiscordActivity
import com.gitlab.kordlib.common.entity.ActivityType
import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.common.ratelimit.BucketRateLimiter
import com.gitlab.kordlib.gateway.DefaultGateway
import com.gitlab.kordlib.gateway.MessageCreate
import com.gitlab.kordlib.gateway.UpdateStatus
import com.gitlab.kordlib.gateway.retry.LinearRetry
import com.gitlab.kordlib.gateway.start
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.UnstableDefault
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds
import kotlin.time.toKotlinDuration

@FlowPreview
@UnstableDefault
@KtorExperimentalAPI
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class DefaultGatewayTest {
    @Test
    @Disabled
    @ExperimentalTime
    fun `default gateway functions normally`() {
        val token = System.getenv("token")

        val gateway = DefaultGateway {
            url = "wss://gateway.discord.gg/"
            client = HttpClient(CIO) {
                install(WebSockets)
                install(JsonFeature)
            }

            reconnectRetry = LinearRetry(2.seconds, 20.seconds, 10)
            sendRateLimiter = BucketRateLimiter(120, Duration.ofSeconds(60).toKotlinDuration())
        }

        gateway.events.filterIsInstance<MessageCreate>().flowOn(Dispatchers.Default).onEach {
            val words = it.message.content.split(' ')
            when (words.firstOrNull()) {
                "!close" -> gateway.stop()
                "!restart" -> gateway.restart()
                "!detach" -> gateway.detach()
                "!status" -> when (words.getOrNull(1)) {
                    "playing" -> gateway.send(UpdateStatus(status = Status.Online, afk = false, game = DiscordActivity("Kord", ActivityType.Game)))
                }
                "!ping" ->  gateway.send(UpdateStatus(status = Status.Online, afk = false, game = DiscordActivity("Ping is ${gateway.ping.toLongMilliseconds()}", ActivityType.Game)))
            }
        }.launchIn(GlobalScope)

        runBlocking {
            gateway.start(token)
        }
    }
}