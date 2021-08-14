package dev.kord.voice

/**
 * Implementations of [AudioProvider] should provide proper [AudioFrame]s representing the audio
 * which should be transmitted to Discord.
 */
fun interface AudioProvider {
    /**
     * Provides a single frame of audio, [AudioFrame].
     *
     * @return the frame of audio.
     */
    fun provide(): AudioFrame?
}