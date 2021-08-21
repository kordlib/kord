package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * Implementations of [AudioProvider] should provide proper [AudioFrame]s representing the audio
 * which should be transmitted to Discord.
 */
@KordVoice
interface AudioProvider { // we can't make this a fun interface without breaking the IR compiler
    /**
     * Provides a single frame of audio, [AudioFrame].
     *
     * @return the frame of audio.
     */
    fun provide(): AudioFrame?

    /**
     * Polls [AudioFrame]s into the [frames] channel at an appropriate interval. Suspends until the coroutine scope is cancelled.
     *
     * @param frames the channel where [AudioFrame]s will be sent to.
     */
    suspend fun provideFrames(frames: SendChannel<AudioFrame?>) = coroutineScope {
        val mark = TimeSource.Monotonic.markNow()
        var nextFrameTimestamp = mark.elapsedNow().inWholeNanoseconds

        while (isActive) {
            nextFrameTimestamp += Duration.milliseconds(20).inWholeNanoseconds
            delayUntilNextFrameTimestamp(mark, nextFrameTimestamp)
            frames.send(provide())
        }
    }
}

private suspend inline fun delayUntilNextFrameTimestamp(mark: TimeMark, nextFrameTimestamp: Long) {
    delay(Duration.nanoseconds(max(0, nextFrameTimestamp - mark.elapsedNow().inWholeNanoseconds)).inWholeMilliseconds)
}

@KordVoice
inline fun AudioProvider(crossinline provider: () -> AudioFrame?) = object : AudioProvider {
    override fun provide(): AudioFrame? = provider()
}