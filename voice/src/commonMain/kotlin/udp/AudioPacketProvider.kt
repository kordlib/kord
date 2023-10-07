package dev.kord.voice.udp

import dev.kord.voice.encryption.strategies.NonceStrategy
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor

public abstract class AudioPacketProvider(public val key: ByteArray, public val nonceStrategy: NonceStrategy) {
    public abstract fun provide(sequence: UShort, timestamp: UInt, ssrc: UInt, data: ByteArray): ByteArrayView
}

internal class CouldNotEncryptDataException(val data: ByteArray) :
    RuntimeException("Couldn't encrypt the following data: [${data.joinToString(", ")}]")

internal fun MutableByteArrayCursor.writeHeader(sequence: Short, timestamp: Int, ssrc: Int) {
    writeByte(((2 shl 6) or (0x0) or (0x0)).toByte()) // first 2 bytes are version. the rest
    writeByte(PayloadType.Audio.raw)
    writeShort(sequence)
    writeInt(timestamp)
    writeInt(ssrc)
}

public expect class DefaultAudioPacketProvider(key: ByteArray, nonceStrategy: NonceStrategy) : AudioPacketProvider
