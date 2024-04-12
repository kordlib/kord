package dev.kord.gateway

import dev.kord.common.entity.PresenceStatus
import dev.kord.common.ratelimit.IntervalRateLimiter
import dev.kord.gateway.retry.LinearRetry
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.seconds

suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("expected a token")

    val gateway = DefaultGateway {
        reconnectRetry = LinearRetry(2.seconds, 20.seconds, 10)
        sendRateLimiter = IntervalRateLimiter(limit = 120, interval = 60.seconds)
    }

    gateway.events.filterIsInstance<MessageCreate>().onEach {
        val words = it.message.content.split(' ')
        when (words.firstOrNull()) {
            "!close"  -> gateway.stop()
            "!detach" -> gateway.detach()
            "!status" -> when (words.getOrNull(1)) {
                "playing" -> gateway.editPresence {
                    status = PresenceStatus.Online
                    afk = false
                    playing("Kord")
                }
            }

            "!ping"   -> gateway.editPresence {
                status = PresenceStatus.Online
                afk = false
                listening("a ${gateway.ping.value?.inWholeMilliseconds} ms ping")
            }
        }
    }.launchIn(gateway)

    gateway.start(token) {
        @OptIn(PrivilegedIntent::class)
        intents = Intents.ALL
    }
}
