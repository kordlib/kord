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
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.coroutines.CoroutineContext


@UnstableDefault
@ImplicitReflectionSerializer
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class DiscordWebSocket(val ws: WebSocketSession) : CoroutineScope {
    init {
        heartBeat()
        ws.outgoing.invokeOnClose { job.cancel() }
    }


    private val job = Job() + newSingleThreadContext("WebSocketThread")
    override val coroutineContext: CoroutineContext = job
    val incoming = ws.incoming.map { it.payload() }
    suspend inline fun send(payload: Payload) = ws.send(Frame.Text(payload.stringify()))
    private suspend fun getInterval(): Long {
        val hello = incoming.first { it.opCode == OpCode.Hello }
        return hello.primitive.long
    }

    private fun heartBeat() = launch {
        ticker(getInterval()).consumeEach { send(payload(OpCode.Heartbeat, Cache.sequance.primitive())) }
    }


}

@ImplicitReflectionSerializer
fun payload(opCode:OpCode, data:JsonElement, sequence:Int? = null, name:String? = null) = Payload(opCode, data, sequence, name)

@UnstableDefault
@ImplicitReflectionSerializer
fun Frame.payload(): Payload {
    val element = Json.plain.parseJson((this as Frame.Text).readText())
    return Json.plain.fromJson(Payload.serializer(), element)
}


fun Number?.primitive() = JsonPrimitive(this)
fun String?.primitive() = JsonPrimitive(this)
fun Boolean?.primitive() = JsonPrimitive(this)



