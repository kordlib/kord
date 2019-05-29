package com.gitlab.hopebaron.websocket

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.first
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.channels.ticker
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext


@UnstableDefault
@ImplicitReflectionSerializer
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class DiscordWebSocket(private val ws: WebSocketSession) : CoroutineScope {
    private val sequence: Int? = null

    init {
        heartBeat()
        ws.outgoing.invokeOnClose { job.cancel() }
    }


    private val job = Job() + Dispatchers.IO
    override val coroutineContext: CoroutineContext = job
    val incoming = ws.incoming.map { it.payload() }
    suspend fun send(payload: Payload) = ws.send(Frame.Text(payload.stringify()))
    private suspend fun getInterval(): Long {
        val hello = incoming.first { it.opCode == OpCode.Hello }
        return hello.data!!.primitive.long
    }

    private fun heartBeat() = launch {
        ticker(getInterval()).consumeEach { send(Payload(OpCode.Heartbeat, sequence)) }
    }


}

@UnstableDefault
@ImplicitReflectionSerializer
fun Frame.payload(): Payload {
    val element = Json.plain.parseJson((this as Frame.Text).readText())
    return Json.plain.fromJson(Payload.serializer(), element)
}

@UnstableDefault
@ImplicitReflectionSerializer
private fun Payload.stringify() = Json.stringify(Payload.serializer(), this)




