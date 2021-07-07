package dev.kord.voice

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

val SILENCE_BYTES = byteArrayOf(0xFC.toByte(), 0xFF.toByte(), 0xFE.toByte())

sealed class AudioFrame {
    object Wait : AudioFrame()
    class Frame(val data: ByteArray) : AudioFrame() {
        override fun toString(): String = data.joinToString(", ")
    }
    object Silence : AudioFrame() {
        val data = SILENCE_BYTES
    }
}

interface AudioProvider {
    val frames: ReceiveChannel<AudioFrame>
}





