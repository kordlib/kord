package dev.kord.voice.gateway

import dev.kord.voice.command.VoiceCommand
import dev.kord.voice.event.VoiceEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface VoiceGateway : CoroutineScope {

    val events: Flow<VoiceEvent>

    suspend fun connect()

    fun resume()

    fun send(command: VoiceCommand)

    fun disconnect()
}