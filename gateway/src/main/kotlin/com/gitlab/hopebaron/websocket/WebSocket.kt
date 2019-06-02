package com.gitlab.hopebaron.websocket

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.map
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Companion.stringify
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

    suspend fun send(payload: SendPayload) = ws.send(payload.stringify())


}

@UnstableDefault
fun Frame.payload(): ReceivePayload {
    val element = Json.plain.parseJson((this as Frame.Text).readText())
    return Json.plain.fromJson(ReceivePayload.serializer(), element)
}

@UnstableDefault
private fun SendPayload.stringify() = stringify(SendPayload.serializer(), this)



