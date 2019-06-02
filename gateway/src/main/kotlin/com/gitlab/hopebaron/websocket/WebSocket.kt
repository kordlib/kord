package com.gitlab.hopebaron.websocket

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.channels.ticker
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

// TODO("Current Class is broken.")
@UnstableDefault
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class DiscordWebSocket(private val ws: WebSocketSession) : CoroutineScope {
    private val sequence: Int? = null
    private val job = Job() + Dispatchers.IO
    override val coroutineContext: CoroutineContext = job
    val incoming = ws.incoming.map { it.payload() }

    init {
        heartBeat()
        ws.outgoing.invokeOnClose { job.cancel() }
    }

    suspend fun send(payload: SendPayload) {
        val json = TODO()
        ws.send(Frame.Text(json))
    }

    private suspend fun getInterval(): Long {
        TODO()
    }

    private fun heartBeat() = launch {
        ticker(getInterval()).consumeEach { send(SendPayload(OpCode.Heartbeat, TODO("Need a "))) }
    }


}

@UnstableDefault
fun Frame.payload(): ReceivePayload {
    val element = Json.plain.parseJson((this as Frame.Text).readText())
    return Json.plain.fromJson(ReceivePayload.serializer(), element)
}

@UnstableDefault
private fun ReceivePayload.stringify() = Json.stringify(ReceivePayload.serializer(), this)

fun SendPayload.stringify() = Unit



