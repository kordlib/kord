package gateway

import com.gitlab.hopebaron.websocket.*
import com.gitlab.hopebaron.websocket.entity.Activity
import com.gitlab.hopebaron.websocket.entity.ActivityType
import com.gitlab.hopebaron.websocket.ratelimit.BucketRateLimiter
import com.gitlab.hopebaron.websocket.retry.LinearRetry
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.UnstableDefault
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.spekframework.spek2.style.specification.xdescribe
import java.time.Duration
import kotlin.coroutines.CoroutineContext

@FlowPreview
@UnstableDefault
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class DefaultGatewayTest : Spek({

    xdescribe("a default gateway") {
        val url = "gateway.discord.gg"
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
})