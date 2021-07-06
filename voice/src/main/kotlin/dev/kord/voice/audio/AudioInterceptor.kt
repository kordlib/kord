package dev.kord.voice.audio

import dev.kord.voice.AudioFrame
import dev.kord.voice.AudioProvider
import dev.kord.voice.gateway.VoiceGateway

class AudioInterceptor(private val voiceGateway: VoiceGateway, private val audioProvider: AudioProvider) {

    suspend fun start() {
        for (frame in audioProvider.provide()) {
          when(frame) {
              is AudioFrame.Silence -> voiceGateway.sendEncryptedVoice(frame.data)
              is AudioFrame.Frame -> voiceGateway.sendEncryptedVoice(frame.data)
              else -> Unit
          }
        }
    }
}