package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import dev.kord.gateway.Gateway
import dev.kord.voice.gateway.SendSpeaking
import dev.kord.voice.gateway.VoiceGateway
import kotlin.properties.Delegates

/**
 * Variables that are accessible to any FrameInterceptor through the [VoiceConnection.frameInterceptorFactory].
 *
 * @param gateway the gateway that handles the guild this voice connection is connected to.
 * @param voiceGateway the underlying [VoiceGateway].
 * @param ssrc the current SSRC retrieved from Discord.
 */
@KordVoice
data class FrameInterceptorContext(
    val gateway: Gateway,
    val voiceGateway: VoiceGateway,
    val ssrc: UInt,
)

@KordVoice
class FrameInterceptorContextBuilder(var gateway: Gateway, var voiceGateway: VoiceGateway) {
    var ssrc: UInt by Delegates.notNull()

    fun build() = FrameInterceptorContext(gateway, voiceGateway, ssrc)
}

@KordVoice
internal inline fun FrameInterceptorContext(gateway: Gateway, voiceGateway: VoiceGateway, builder: FrameInterceptorContextBuilder.() -> Unit) =
    FrameInterceptorContextBuilder(gateway, voiceGateway).apply(builder).build()

/**
 * A interceptor for audio frames before they are sent as packets.
 *
 * @see DefaultFrameInterceptor
 */
@KordVoice
fun interface FrameInterceptor {
    suspend fun intercept(audioFrame: AudioFrame?): AudioFrame?
}

/**
 * The default implementation for [FrameInterceptor].
 * Any custom implementation should extend this and call the super [intercept] method, or else
 * the speaking flags will not be sent!
 *
 * @param connection the voice connection.
 * @param speakingState the speaking state that will be used when there is audio data to be sent. By default, it is microphone-only.
 */
@KordVoice
open class DefaultFrameInterceptor(
    protected val context: FrameInterceptorContext,
    private val speakingState: SpeakingFlags = SpeakingFlags { +SpeakingFlag.Microphone }
) : FrameInterceptor {
    private var framesOfSilence = 5
    private var isSpeaking = false

    override suspend fun intercept(audioFrame: AudioFrame?): AudioFrame? = with(context) {
        var frame: AudioFrame? = null

        if (audioFrame != null || framesOfSilence > 0) {
            if (!isSpeaking && audioFrame != null) {
                isSpeaking = true
                voiceGateway.send(SendSpeaking(speakingState, 0, ssrc))
            }

            frame = audioFrame ?: AudioFrame.SILENCE

            if (audioFrame == null) {
                framesOfSilence--
                if (framesOfSilence == 0) {
                    isSpeaking = false
                    voiceGateway.send(SendSpeaking(SpeakingFlags(0), 0, ssrc))
                }
            } else {
                framesOfSilence = 5
            }
        }

        return frame
    }
}