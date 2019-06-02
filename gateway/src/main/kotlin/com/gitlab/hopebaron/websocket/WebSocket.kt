package com.gitlab.hopebaron.websocket

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.map
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

@UnstableDefault
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class DiscordWebSocket(private val ws: WebSocketSession) : CoroutineScope {
    private val sequence: Int? = null
    override val coroutineContext: CoroutineContext get() = Job() + Dispatchers.IO
    val incoming = ws.incoming.map { it.payload() }

    init {
        TODO("make this class work")
    }

    suspend fun send(payload: SendPayload) {
        TODO()
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



