package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
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
fun interface AudioProvider {
    /**
     * Provides a single frame of audio, [AudioFrame].
     *
     * @return the frame of audio.
     */
    suspend fun provide(): AudioFrame?

    /**
     * Polls [AudioFrame]s into the [frames] channel at an appropriate interval. Suspends until the coroutine scope is cancelled.
     *
     * @param frames the channel where [AudioFrame]s will be sent to.
     */
    suspend fun CoroutineScope.provideFrames(frames: SendChannel<AudioFrame?>) {
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