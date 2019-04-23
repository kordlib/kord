import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.channels.ticker
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Companion.stringify
import kotlin.coroutines.CoroutineContext
import io.ktor.client.features.json.JsonFeature


@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class DiscordWebSocket(val ws: WebSocketSession, interval: Long) : CoroutineScope {
    init {
        heartBeat(interval)
        ws.outgoing.invokeOnClose { job.cancel() }
    }


    private val job = Job() + newSingleThreadContext("WebSocketThread")
    override val coroutineContext: CoroutineContext = job
    val incoming = ws.incoming.map { if (it is Frame.Text) it.event() }

    suspend inline fun send(event: Event) = ws.send(Frame.Text(event.stringify()))


    private fun heartBeat(interval: Long) {
        with(ticker(interval)) {
            launch { consumeEach { send(HeartBeat()) } }
        }
    }

}


internal fun Frame.event() = Json.parse(Event.serializer(), (this as Frame.Text).readText())
fun Event.stringify(): String = stringify(Event.serializer(), this)

@KtorExperimentalAPI
val client = HttpClient(CIO) {
    install(JsonFeature)


    install(WebSockets)
}

