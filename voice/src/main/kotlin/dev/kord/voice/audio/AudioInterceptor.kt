package dev.kord.voice.audio

import dev.kord.voice.AudioFrame
import dev.kord.voice.AudioProvider
import dev.kord.voice.command.VoiceSpeakingCommand
import dev.kord.voice.gateway.DefaultVoiceGateway

class AudioInterceptor(private val voiceGateway: DefaultVoiceGateway, private val audioProvider: AudioProvider) {
    suspend fun start() {
        println("let the silence begin")
        repeat(5) {
            voiceGateway.sendEncryptedVoice(AudioFrame.Silence.data)
        }

        println("and now we play")
        // dont ratelimit ourselves by spamming voice updates...
        // for proper impl see https://github.com/lost-illusi0n/ktcord-snapshot/blob/1086c5b3e4560f7b6d8e6a3885a6e31b607612eb/ktcord-audio/src/commonMain/kotlin/net/lostillusion/ktcord/audio/udp/DiscordAudioUdpConnection.kt#L87-L102
        voiceGateway.send(VoiceSpeakingCommand(5, 20, voiceGateway.ssrc!!))
        for (frame in audioProvider.frames) {
            when (frame) {
                is AudioFrame.Silence -> voiceGateway.sendEncryptedVoice(frame.data)
                is AudioFrame.Frame -> {
                    voiceGateway.sendEncryptedVoice(frame.data)
                }
                else -> Unit
            }
        }
    }
}