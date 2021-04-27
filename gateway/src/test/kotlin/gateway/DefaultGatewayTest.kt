package gateway

import dev.kord.common.entity.ActivityType
import dev.kord.common.entity.PresenceStatus
import dev.kord.common.entity.DiscordBotActivity
import dev.kord.common.ratelimit.BucketRateLimiter
import dev.kord.gateway.*
import dev.kord.gateway.retry.LinearRetry
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Duration as KDuration
import kotlin.time.toKotlinDuration

@FlowPreview
@KtorExperimentalAPI
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class DefaultGatewayTest {
    @Test
    @Disabled
    @ExperimentalTime
    fun `default gateway functions correctly`() {
        val token = System.getenv("KORD_TEST_TOKEN")

        val gateway = DefaultGateway {
            client = HttpClient(CIO) {
                install(WebSockets)
                install(JsonFeature) {
                    serializer = KotlinxSerializer(Json)
                }
            }

            reconnectRetry = LinearRetry(KDuration.seconds(2), KDuration.seconds(20), 10)
            sendRateLimiter = BucketRateLimiter(120, Duration.ofSeconds(60).toKotlinDuration())
        }

        gateway.events.filterIsInstance<MessageCreate>().flowOn(Dispatchers.Default).onEach {
            val words = it.message.content.split(' ')
            when (words.firstOrNull()) {
                "!close" -> gateway.stop()
                "!restart" -> gateway.restart(Close.Reconnecting)
                "!detach" -> gateway.detach()
                "!status" -> when (words.getOrNull(1)) {
                    "playing" -> gateway.send(UpdateStatus(status = PresenceStatus.Online, afk = false, activities = listOf(DiscordBotActivity("Kord", ActivityType.Game)), since = null))
                }
                "!ping" -> gateway.send(UpdateStatus(status = PresenceStatus.Online, afk = false, activities = listOf(DiscordBotActivity("Ping is ${gateway.ping.value?.toLongMilliseconds()}", ActivityType.Game)), since = null))
            }
        }.launchIn(GlobalScope)

        runBlocking {
            gateway.start(token) {
                @OptIn(PrivilegedIntent::class)
                intents = Intents.all
            }
        }
    }
}
