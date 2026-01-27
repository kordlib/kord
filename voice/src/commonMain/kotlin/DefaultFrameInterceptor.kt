package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.gateway.SendSpeaking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@KordVoice
/**
 * Data that is used to configure for the lifetime of a [DefaultFrameInterceptor].
 *
 * @param speakingState the [SpeakingFlags] to be sent when there audio being sent. By default, it is [microphone-only][SpeakingFlag.Microphone].
 */
public data class DefaultFrameInterceptorData(
    val speakingState: SpeakingFlags = SpeakingFlags { +SpeakingFlag.Microphone }
)

private const val FRAMES_OF_SILENCE_TO_PLAY = 5

@KordVoice
/**
 * The default implementation for [FrameInterceptor].
 * Any custom implementation should extend this and call the super [intercept] method, or else
 * the speaking flags will not be sent!
 *
 * @param data the data to configure this instance with.
 */
public class DefaultFrameInterceptor(private val data: DefaultFrameInterceptorData = DefaultFrameInterceptorData()) :
    FrameInterceptor {
    override fun Flow<AudioFrame?>.intercept(configuration: FrameInterceptorConfiguration): Flow<AudioFrame?> {
        var framesOfSilence = 5
        var isSpeaking = false

        suspend fun startSpeaking() {
            isSpeaking = true
            configuration.voiceGateway.send(SendSpeaking(data.speakingState, 0, configuration.ssrc))
        }

        suspend fun stopSpeaking() {
            isSpeaking = false
            configuration.voiceGateway.send(SendSpeaking(SpeakingFlags(0), 0, configuration.ssrc))
        }

        return onEach { frame ->
            if (frame != null && !isSpeaking) {
                framesOfSilence = FRAMES_OF_SILENCE_TO_PLAY
                startSpeaking()
            } else if ((frame == null) && isSpeaking && (--framesOfSilence == 0)) {
                stopSpeaking()
            }
        }.map { frame ->
            when (framesOfSilence) {
                0 -> frame
                else -> frame ?: AudioFrame.SILENCE
            }
        }
    }
}