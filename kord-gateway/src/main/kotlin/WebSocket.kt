import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.channels.ticker
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Companion.stringify
import kotlin.coroutines.CoroutineContext

@ObsoleteCoroutinesApi
class DiscordWebSocket(val ws: WebSocketSession, private val interval: Long) : CoroutineScope {
    init {
        heartBeat(interval)
        ws.outgoing.invokeOnClose { job.cancel() }
    }



    private val job = Job() + newSingleThreadContext("WebSocketThread")
    override val coroutineContext: CoroutineContext = job
    val incoming = ws.incoming.map { if (it is Frame.Text) it.event() }


    suspend inline fun send(event: Event) = ws.send(Frame.Text(event.stringify()))


    private fun heartBeat(interval: Long) = launch {
        while (isAcitve) {
            send(HeartBeat)
            delay(interval)
        }
    }

}


internal fun Frame.event() = Json.parse(Event.serializer(), (this as Frame.Text).readText())
fun Event.stringify(): String = stringify(Event.serializer(), this)