import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Companion.stringify
import kotlin.coroutines.CoroutineContext

@ObsoleteCoroutinesApi
class DiscordWebSocket(val ws: WebSocketSession,val interval:Long) : CoroutineScope {
    init {
        heartBeat(interval)
    }
    private val job = Job() + newSingleThreadContext("WebSocketThread")
    override val coroutineContext: CoroutineContext = job
    val incoming = ws.incoming.map { if (it is Frame.Text) it.event() }
    suspend inline fun send(event: Event) {
        val event = stringify(Event.serializer(), event)
        ws.send(Frame.Text(event))
    }

    private fun heartBeat(interval: Long) = launch {
        send()
        delay(interval)
    }

}


internal fun Frame.event() = Json.parse(Event.serializer(), (this as Frame.Text).readText())
internal fun Event.frame(): Frame.Text = Frame.Text(stringify(Event.serializer(), this))