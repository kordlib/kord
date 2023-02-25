import dev.kord.common.entity.PresenceStatus
import dev.kord.common.ratelimit.IntervalRateLimiter
import dev.kord.gateway.*
import dev.kord.gateway.retry.LinearRetry
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
suspend fun main() {
    //<editor-fold desc="token">
    val token = "NTM1MTI5NDA2NjUwMzE4ODYw.GAQrFJ.r0EwX1yDw_2W2ERHl9vnvtQjxqw88vxCOhDskA"
    //</editor-fold>

    val gateway = DefaultGateway {
        reconnectRetry = LinearRetry(2.seconds, 20.seconds, 10)
        sendRateLimiter = IntervalRateLimiter(limit = 120, interval = 60.seconds)
    }

    gateway.events.filterIsInstance<MessageCreate>().onEach {
        val words = it.message.content.split(' ')
        when (words.firstOrNull()) {
            "!close" -> gateway.stop()
            "!detach" -> gateway.detach()
            "!status" -> when (words.getOrNull(1)) {
                "playing" -> gateway.editPresence {
                    status = PresenceStatus.Online
                    afk = false
                    playing("Kord")
                }
            }

            "!ping" -> gateway.editPresence {
                status = PresenceStatus.Online
                afk = false
                listening("a ${gateway.ping.value?.inWholeMilliseconds} ms ping")
            }
        }
    }.launchIn(gateway)

    gateway.start(token) {
        @OptIn(PrivilegedIntent::class)
        intents = Intents.all
    }
}
//DEBUG: [Function] Identifying on shard 0 with rate_limit_key 0...
//TRACE: [Function] opening gateway connection to wss://gateway.discord.gg/?v=10&encoding=json&compress=zlib-stream
//TRACE: Sending WebSocket request [object Object]
//TRACE: Receive websocket session from wss://gateway.discord.gg/?v=10&encoding=json&compress=zlib-stream: [object Object]
//TRACE: [Function] Received raw frame: Frame BINARY (fin=false, buffer len = 114)
//TRACE: [Function] Gateway <<< {"t":null,"s":null,"op":10,"d":{"heartbeat_interval":41250,"_trace":["[\"gateway-prd-us-east1-d-bdzc\",{\"micros\":0.0}]"]}}
//TRACE: [Function] Gateway >>> {"op":2,"d":{"token":"token","properties":{"os":"win32","browser":"Kord","device":"Kord"},"compress":false,"large_threshold":250,"shard":[0,1],"intents":"3276799"}}
//TRACE: [Function] Gateway >>> {"op":1,"d":null}
//TRACE: [Function] Gateway >>> {"op":1,"d":null}
//TRACE: [Function] Received raw frame: Frame BINARY (fin=false, buffer len = 15)
//DEBUG: [Function] Identifying on shard 0 timed out, delaying 5s before freeing up rate_limit_key 0
