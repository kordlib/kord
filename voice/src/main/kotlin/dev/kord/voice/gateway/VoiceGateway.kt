package dev.kord.voice.gateway


import dev.kord.voice.command.VoiceCommand
import dev.kord.voice.event.VoiceEvent
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mu.KotlinLogging

@PublishedApi
internal val gatewayOnLogger = KotlinLogging.logger("VoiceGateway.on")

interface VoiceGateway : CoroutineScope {

    val events: Flow<VoiceEvent>

    suspend fun connect()

    suspend fun resume()

    suspend fun send(command: VoiceCommand): Boolean

    suspend fun disconnect()

    suspend fun sendEncryptedVoice(data: ByteArray)
}

inline fun <reified T : VoiceEvent> VoiceGateway.on(
    scope: CoroutineScope = this,
    crossinline consumer: suspend T.() -> Unit
): Job {
    return this.events.buffer(Channel.UNLIMITED).filterIsInstance<T>().onEach {
        launch { it.runCatching { it.consumer() }.onFailure(gatewayOnLogger::error) }
    }.launchIn(scope)
}
