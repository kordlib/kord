import com.gitlab.kordlib.common.entity.Activity
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
import java.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds
import kotlin.time.toKotlinDuration

@FlowPreview
@UnstableDefault
@ExperimentalTime
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
suspend fun main() {
    val token = System.getenv("token")

    val gateway = DefaultGateway {
        url = "wss://gateway.discord.gg/"
        client = HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature)
        }

        retry = LinearRetry(2.seconds, 20.seconds, 10)
        rateLimiter = BucketRateLimiter(120, Duration.ofSeconds(60).toKotlinDuration())
    }

    gateway.events.filterIsInstance<MessageCreate>().flowOn(Dispatchers.Default).onEach {
        val words = it.message.content.split(' ')
        when (words.firstOrNull()) {
            "!close" -> gateway.stop()
            "!restart" -> gateway.restart()
            "!detach" -> gateway.detach()
            "!status" -> when (words.getOrNull(1)) {
                "playing" -> gateway.send(UpdateStatus(status = Status.Online, afk = false, game = Activity("Kord", ActivityType.Game)))
            }
        }
    }.launchIn(GlobalScope)

    gateway.start(token)
}