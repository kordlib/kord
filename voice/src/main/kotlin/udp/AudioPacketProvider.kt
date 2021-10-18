package dev.kord.voice.udp

import com.iwebpp.crypto.TweetNaclFast
import dev.kord.voice.encryption.XSalsa20Poly1305Codec
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view

abstract class AudioPacketProvider(val key: ByteArray) {
    abstract fun provide(sequence: UShort, timestamp: UInt, ssrc: UInt, data: ByteArray): ByteArrayView
}

private class CouldNotEncryptDataException(val data: ByteArray) :
    RuntimeException("Couldn't encrypt the following data: [${data.joinToString(", ")}]")

class DefaultAudioPackerProvider(key: ByteArray) : AudioPacketProvider(key) {
    private val codec = XSalsa20Poly1305Codec(key)

    private var nonce: Int = 0

    private val packetBuffer = ByteArray(2048)
    private val packetBufferCursor: MutableByteArrayCursor = packetBuffer.mutableCursor()
    private val packetBufferView: ByteArrayView = packetBuffer.view()
    private val nonceBuffer: MutableByteArrayCursor = ByteArray(TweetNaclFast.SecretBox.nonceLength).mutableCursor()

    private val lock: Any = Any()

    private fun loadNonce() {
        // reset cursor position to 0
        nonceBuffer.reset()

        // write the 4 byte nonce
        nonceBuffer.writeInt(nonce)

        // increment it for next call
        nonce++
    }

    private fun MutableByteArrayCursor.writeHeader(sequence: Short, timestamp: Int, ssrc: Int) {
        writeByte(((2 shl 6) or (0x0) or (0x0)).toByte()) // first 2 bytes are version. the rest
        writeByte(PayloadType.Audio.raw)
        writeShort(sequence)
        writeInt(timestamp)
        writeInt(ssrc)
    }

    override fun provide(sequence: UShort, timestamp: UInt, ssrc: UInt, data: ByteArray): ByteArrayView =
        synchronized(lock) {
            with(packetBufferCursor) {
                reset()
                loadNonce()

                // encrypt data
                val encryptedStart = cursor
                val encrypted = codec.encrypt(data, nonce = nonceBuffer.data, output = this)
                val encryptedLength = cursor - encryptedStart

                if (!encrypted) throw CouldNotEncryptDataException(data)

                // let's keep track of where the actual packet starts in the buffer
                val initial = cursor

                // make sure we enough room in this buffer
                resize(cursor + RTP_HEADER_LENGTH + encryptedLength)

                writeHeader(sequence.toShort(), timestamp.toInt(), ssrc.toInt())
                writeByteArray(this.data, encryptedStart, encryptedLength)

                // let's make sure we have the correct view of the packet
                if (!packetBufferView.resize(initial, cursor)) error("couldn't resize packet buffer view")

                packetBufferView
            }
        }
}