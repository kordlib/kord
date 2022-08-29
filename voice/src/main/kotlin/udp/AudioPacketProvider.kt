package dev.kord.voice.udp

import com.iwebpp.crypto.TweetNaclFast
import dev.kord.voice.encryption.XSalsa20Poly1305Codec
import dev.kord.voice.encryption.strategies.NonceStrategy
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view

public abstract class AudioPacketProvider(public val key: ByteArray, public val nonceStrategy: NonceStrategy) {
    public abstract fun provide(sequence: UShort, timestamp: UInt, ssrc: UInt, data: ByteArray): ByteArrayView
}

private class CouldNotEncryptDataException(val data: ByteArray) :
    RuntimeException("Couldn't encrypt the following data: [${data.joinToString(", ")}]")

public class DefaultAudioPacketProvider(key: ByteArray, nonceStrategy: NonceStrategy) :
    AudioPacketProvider(key, nonceStrategy) {
    private val codec = XSalsa20Poly1305Codec(key)

    private val packetBuffer = ByteArray(2048)
    private val packetBufferCursor: MutableByteArrayCursor = packetBuffer.mutableCursor()
    private val packetBufferView: ByteArrayView = packetBuffer.view()

    private val rtpHeaderView: ByteArrayView = packetBuffer.view(0, RTP_HEADER_LENGTH)!!

    private val nonceBuffer: MutableByteArrayCursor = ByteArray(TweetNaclFast.SecretBox.nonceLength).mutableCursor()

    private val lock: Any = Any()

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
                this.reset()
                nonceBuffer.reset()

                // make sure we enough room in this buffer
                resize(RTP_HEADER_LENGTH + (data.size + TweetNaclFast.SecretBox.boxzerobytesLength) + nonceStrategy.nonceLength)

                // write header and generate nonce
                writeHeader(sequence.toShort(), timestamp.toInt(), ssrc.toInt())

                val rawNonce = nonceStrategy.generate { rtpHeaderView }
                nonceBuffer.writeByteView(rawNonce)

                // encrypt data and write into our buffer
                val encrypted = codec.encrypt(data, nonce = nonceBuffer.data, output = this)

                if (!encrypted) throw CouldNotEncryptDataException(data)

                nonceStrategy.append(rawNonce, this)

                // let's make sure we have the correct view of the packet
                if (!packetBufferView.resize(0, cursor)) error("couldn't resize packet buffer view?!")

                packetBufferView
            }
        }
}