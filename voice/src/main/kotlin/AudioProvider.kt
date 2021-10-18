package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.max
import kotlin.time.TimeSource

/**
 * Implementations of [AudioProvider] should provide proper [AudioFrame]s representing the audio
 * which should be transmitted to Discord.
 */
@KordVoice
public fun interface AudioProvider {
    /**
     * Provides a single frame of audio, [AudioFrame].
     *
     * @return the frame of audio.
     */
    public suspend fun provide(): AudioFrame?

    /**
     * Polls [AudioFrame]s into the [frames] channel at an appropriate interval. Suspends until the coroutine scope is cancelled.
     *
     * @param frames the channel where [AudioFrame]s will be sent to.
     */
    public suspend fun CoroutineScope.provideFrames(frames: SendChannel<AudioFrame?>) {
        val mark = TimeSource.Monotonic.markNow()
        var nextFrameTimestamp = mark.elapsedNow().inWholeNanoseconds

        while (isActive) {
            frames.send(provide())

            nextFrameTimestamp += 20_000_000
            delayUntilNextFrameTimestamp(mark.elapsedNow().inWholeNanoseconds, nextFrameTimestamp)
        }
    }
}

private suspend inline fun delayUntilNextFrameTimestamp(now: Long, nextFrameTimestamp: Long) {
    delay(max(0, nextFrameTimestamp - now) / 1_000_000)
}