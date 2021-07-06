package dev.kord.voice.audio

import dev.kord.voice.AudioFrame
import dev.kord.voice.AudioProvider
import dev.kord.voice.VoiceOpCode
import dev.kord.voice.command.VoiceSpeakingCommand
import dev.kord.voice.gateway.DefaultVoiceGateway
import dev.kord.voice.gateway.VoiceGateway

class AudioInterceptor(private val voiceGateway: DefaultVoiceGateway, private val audioProvider: AudioProvider) {

    suspend fun start() {
        for (frame in audioProvider.frames) {
          when(frame) {
              is AudioFrame.Silence -> voiceGateway.sendEncryptedVoice(frame.data)
              is AudioFrame.Frame -> {
                  voiceGateway.send(VoiceSpeakingCommand(5,20, voiceGateway.ssrc!!))
                  voiceGateway.sendEncryptedVoice(frame.data)
              }
              else -> Unit
          }
        }
    }
}