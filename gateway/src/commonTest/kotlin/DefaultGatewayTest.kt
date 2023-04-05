package dev.kord.gateway

import dev.kord.common.entity.ActivityType
import dev.kord.common.entity.DiscordBotActivity
import dev.kord.common.entity.PresenceStatus
import dev.kord.common.ratelimit.IntervalRateLimiter
import dev.kord.gateway.*
import dev.kord.gateway.retry.LinearRetry
import dev.kord.test.getEnv
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class DefaultGatewayTest {
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    @JsName("test1")
    @Ignore
    fun `default gateway functions correctly`() = runTest {
        val token = getEnv("KORD_TEST_TOKEN") ?: error("Missing env variable KORD_TEST_TOKEN")

        val gateway = DefaultGateway {
            reconnectRetry = LinearRetry(2.seconds, 20.seconds, 10)
            sendRateLimiter = IntervalRateLimiter(limit = 120, interval = 60.seconds)
        }

        gateway.events.filterIsInstance<MessageCreate>().flowOn(Dispatchers.Default).onEach {
            val words = it.message.content.split(' ')
            when (words.firstOrNull()) {
                "!close" -> gateway.stop()
                "!restart" -> gateway.restart(Close.Reconnecting)
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

        gateway.start(token) {
            @OptIn(PrivilegedIntent::class)
            intents = Intents.all
        }
    }
}
