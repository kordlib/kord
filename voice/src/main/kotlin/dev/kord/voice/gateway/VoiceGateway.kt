package dev.kord.voice.gateway

import dev.kord.gateway.Event
import dev.kord.gateway.Gateway
import dev.kord.gateway.gatewayOnLogger
import dev.kord.gateway.on
import dev.kord.voice.command.VoiceCommand
import dev.kord.voice.event.VoiceEvent
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface VoiceGateway : CoroutineScope {

    val events: Flow<VoiceEvent>

    suspend fun connect()

    fun resume()

    fun send(command: VoiceCommand)

    fun disconnect()
}

inline fun <reified T : VoiceEvent> VoiceGateway.on(
    scope: CoroutineScope = this,
    crossinline consumer: suspend T.() -> Unit
): Job {
    return this.events.buffer(Channel.UNLIMITED).filterIsInstance<T>().onEach {
        launch { it.runCatching { it.consumer() }.onFailure(gatewayOnLogger::error) }
    }.launchIn(scope)
}
