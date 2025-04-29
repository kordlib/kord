package dev.kord.voice.encryption

import com.google.crypto.tink.aead.internal.InsecureNonceXChaCha20
import com.google.crypto.tink.aead.internal.InsecureNonceXChaCha20Poly1305
import com.google.crypto.tink.aead.internal.Poly1305
import dev.kord.voice.udp.DecryptedVoicePacket
import dev.kord.voice.udp.RTP_HEADER_LENGTH
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readTo
import java.nio.ByteBuffer
import javax.crypto.AEADBadTagException

private const val AUTH_TAG_SIZE = Poly1305.MAC_TAG_SIZE_IN_BYTES
private const val NONCE_SIZE = 4
private const val ADDITIONAL_SIZE = RTP_HEADER_LENGTH + AUTH_TAG_SIZE + NONCE_SIZE
private val EMPTY_BYTE_ARRAY = ByteArray(size = 0)

internal class AeadXChaCha20Poly1305RtpSizeVoicePacketCreator(key: ByteArray) : EncryptedVoicePacketCreator {

    // the first 4 bytes are the 32-bit incremental nonce (big endian), the remaining bytes are 0
    private val nonceBuffer = ByteArray(InsecureNonceXChaCha20.NONCE_SIZE_IN_BYTES)
    private var nonce = 0
    private val xChaCha20Poly1305 = InsecureNonceXChaCha20Poly1305(key)
    private val associatedDataBuffer = ByteArray(RTP_HEADER_LENGTH)

    override fun createEncryptedVoicePacket(
        sequence: UShort,
        timestamp: UInt,
        ssrc: UInt,
        audioPlaintext: ByteArray,
    ): ByteArray {
        val nonce = nonce++
        val plaintextSize = audioPlaintext.size
        val packetSize = plaintextSize + ADDITIONAL_SIZE
        val packet = ByteArray(packetSize)

        // write the header into associatedDataBuffer and the voice packet
        associatedDataBuffer.writeRtpHeader(sequence, timestamp, ssrc)
        associatedDataBuffer.copyInto(packet)

        nonceBuffer.writeIntBigEndian(offset = 0, nonce)

        // TODO check if this is true
        // InsecureNonceXChaCha20Poly1305.encrypt requires output.limit() to be set to where the ciphertext will end,
        // otherwise it will read too much when computing the authentication tag. ByteBuffer.wrap with offset and length
        // will set the limit accordingly.
        val output =
            ByteBuffer.wrap(packet, /* offset = */ RTP_HEADER_LENGTH, /* length = */ plaintextSize + AUTH_TAG_SIZE)
        xChaCha20Poly1305.encrypt(output, nonceBuffer, audioPlaintext, associatedDataBuffer)
        val nonceOffset = packetSize - NONCE_SIZE
        check(output.position() == nonceOffset && output.limit() == nonceOffset) {
            "Ciphertext doesn't have the expected length."
        }

        // append the nonce to the end of the voice packet
        packet.writeIntBigEndian(nonceOffset, nonce)

        return packet
    }
}

internal class AeadXChaCha20Poly1305RtpSizeVoicePacketDecryptor(key: ByteArray) : Decrypt() {
    private var ciphertextBuffer: ByteArray = EMPTY_BYTE_ARRAY
    private val nonceBuffer = ByteArray(InsecureNonceXChaCha20.NONCE_SIZE_IN_BYTES)
    private val xChaCha20Poly1305 = InsecureNonceXChaCha20Poly1305(key)

    override fun decrypt(audioPacket: Source): DecryptedVoicePacket? = audioPacket.use { packet ->
        val headerSize = readUnencryptedRtpHeaderPart(packet)
        if (headerSize < 0) {
            return null
        }

        val ciphertext = when (packet) {
            is Buffer -> getCiphertextAndFillNonceBufferFromBuffer(packet)
            else -> getCiphertextAndFillNonceBufferFromSource(packet)
        } ?: return null

        val associatedData = when (headerSize) {
            MAX_UNENCRYPTED_RTP_HEADER_PART_SIZE -> unencryptedRtpHeaderPartBuffer
            else -> unencryptedRtpHeaderPartBuffer.copyOf(headerSize)
        }

        val plaintext = try {
            xChaCha20Poly1305.decrypt(ciphertext, nonceBuffer, associatedData)
        } catch (_: AEADBadTagException) {
            return null
        }

        return createDecryptedVoicePacket(headerSize, extensionAndAudio = plaintext)
    }

    // TODO padding handling

    private fun getCiphertextAndFillNonceBufferFromSource(source: Source): ByteBuffer? {
        val buffer = Buffer()
        try {
            source.transferTo(buffer)
            return getCiphertextAndFillNonceBufferFromBuffer(buffer)
        } finally {
            buffer.clear() // recycle buffer segments
        }
    }

    private fun getCiphertextAndFillNonceBufferFromBuffer(buffer: Buffer): ByteBuffer? {
        val ciphertextSizeLong = buffer.size - NONCE_SIZE
        if (ciphertextSizeLong !in AUTH_TAG_SIZE..Int.MAX_VALUE) {
            return null
        }

        val ciphertextSize = ciphertextSizeLong.toInt()
        val ciphertextBuffer = growCiphertextBuffer(ciphertextSize)

        buffer.readTo(ciphertextBuffer, startIndex = 0, endIndex = ciphertextSize)
        buffer.readTo(nonceBuffer, startIndex = 0, endIndex = NONCE_SIZE)

        return ByteBuffer.wrap(ciphertextBuffer, /* offset = */ 0, /* length = */ ciphertextSize)
    }

    private fun growCiphertextBuffer(ciphertextSize: Int): ByteArray {
        var buffer = ciphertextBuffer
        val bufferSize = buffer.size
        if (bufferSize < ciphertextSize) {
            // preferredSize = bufferSize + (bufferSize / 2) = 1.5 * bufferSize,
            /** see [java.util.ArrayList.grow] and [jdk.internal.util.ArraysSupport.newLength] */
            val preferredSize = bufferSize + (bufferSize shr 1)
            buffer = ByteArray(size = maxOf(ciphertextSize, preferredSize))
            ciphertextBuffer = buffer
        }
        return buffer
    }
}
