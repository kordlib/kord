import dev.kord.common.entity.*
import dev.kord.common.ratelimit.BucketRateLimiter
import dev.kord.gateway.*
import dev.kord.gateway.retry.LinearRetry
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(DelicateCoroutinesApi::class)
@FlowPreview
@ExperimentalTime
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("expected a token")

    val gateway = DefaultGateway {
        client = HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json)
            }
        }

        reconnectRetry = LinearRetry(Duration.seconds(2), Duration.seconds(20), 10)
        sendRateLimiter = BucketRateLimiter(120, Duration.seconds(60))
    }

    gateway.events.filterIsInstance<MessageCreate>().flowOn(Dispatchers.Default).onEach {
        val words = it.message.content.split(' ')
        when (words.firstOrNull()) {
            "!close" -> gateway.stop()
            "!detach" -> gateway.detach()
            "!status" -> when (words.getOrNull(1)) {
                "playing" -> gateway.send(
                    UpdateStatus(
                        status = PresenceStatus.Online,
                        afk = false,
                        activities = listOf(DiscordBotActivity("Kord", ActivityType.Game)),
                        since = null
                    )
                )
            }
            "!ping" -> gateway.send(
                UpdateStatus(
                    status = PresenceStatus.Online,
                    afk = false,
                    activities = listOf(
                        DiscordBotActivity(
                            "Ping is ${gateway.ping.value?.inWholeMilliseconds}",
                            ActivityType.Game
                        )
                    ),
                    since = null
                )
            )
        }
    }.launchIn(GlobalScope)

    runBlocking {
        gateway.start(token) {
            @OptIn(PrivilegedIntent::class)
            intents = Intents.all
        }
    }
}