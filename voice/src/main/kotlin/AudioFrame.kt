package dev.kord.voice

import dev.kord.common.annotation.KordVoice

/**
 * A frame of 20ms Opus-encoded audio data.
 */
@KordVoice
@JvmInline
value class AudioFrame(val data: ByteArray) {
    companion object {
        val SILENCE = AudioFrame(byteArrayOf(0xFC.toByte(), 0xFF.toByte(), 0xFE.toByte()))
    }
}