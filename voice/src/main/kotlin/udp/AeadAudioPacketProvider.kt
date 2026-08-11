package dev.kord.voice.udp

import dev.kord.voice.encryption.AeadEncryption
import dev.kord.voice.encryption.expandNonce
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view

private class CouldNotEncryptAeadDataException(data: ByteArray) :
    RuntimeException("AEAD: Couldn't encrypt data of size ${data.size}")

/**
 * Audio packet provider using AES-256-GCM AEAD encryption (`aead_aes256_gcm_rtpsize` mode).
 *
 * Packet layout: `[RTP Header (12B)] [Encrypted Payload + GCM Tag (16B)] [Nonce (4B)]`
 *
 * The RTP header is used as Additional Authenticated Data (AAD) — authenticated but not encrypted.
 */
public class AeadAudioPacketProvider(key: ByteArray) {
    private val encryption = AeadEncryption(key)
    private var nonceCounter: Int = 0

    private val packetBuffer = ByteArray(2048)
    private val packetBufferCursor: MutableByteArrayCursor = packetBuffer.mutableCursor()
    private val packetBufferView: ByteArrayView = packetBuffer.view()

    private val expandedNonce = ByteArray(AeadEncryption.EXPANDED_NONCE_BYTES)
    private val nonceBytes = ByteArray(AeadEncryption.NONCE_BYTES)

    private val lock: Any = Any()

    private fun MutableByteArrayCursor.writeHeader(sequence: Short, timestamp: Int, ssrc: Int) {
        writeByte(((2 shl 6) or 0x0 or 0x0).toByte()) // version=2, no padding, no extension
        writeByte(PayloadType.Audio.raw)
        writeShort(sequence)
        writeInt(timestamp)
        writeInt(ssrc)
    }

    /**
     * Provide an encrypted RTP audio packet using AES-256-GCM.
     *
     * @param sequence RTP sequence number
     * @param timestamp RTP timestamp
     * @param ssrc synchronization source identifier
     * @param data raw audio payload to encrypt
     * @return a [ByteArrayView] of the complete encrypted packet
     */
    public fun provide(sequence: UShort, timestamp: UInt, ssrc: UInt, data: ByteArray): ByteArrayView =
        synchronized(lock) {
            packetBufferCursor.reset()

            // Ensure buffer is large enough: header(12) + encrypted payload + GCM tag(16) + nonce(4)
            val maxSize = RTP_BASE_HEADER_LENGTH + data.size + AeadEncryption.TAG_BYTES + AeadEncryption.NONCE_BYTES
            packetBufferCursor.resize(maxSize)

            // Write RTP header (12 bytes)
            packetBufferCursor.writeHeader(sequence.toShort(), timestamp.toInt(), ssrc.toInt())

            // Prepare 4-byte nonce (big-endian incrementing counter)
            val nonce = nonceCounter++
            nonceBytes[0] = (nonce shr 24).toByte()
            nonceBytes[1] = (nonce shr 16).toByte()
            nonceBytes[2] = (nonce shr 8).toByte()
            nonceBytes[3] = nonce.toByte()

            // Expand to 12-byte nonce for GCM
            expandNonce(nonceBytes, expandedNonce)

            // Encrypt: plaintext=data, AAD=RTP header (first 12 bytes of packetBuffer)
            val encrypted = encryption.encrypt(
                plaintext = data,
                plaintextOffset = 0,
                plaintextLength = data.size,
                nonce = expandedNonce,
                aad = packetBuffer,
                aadOffset = 0,
                aadLength = RTP_BASE_HEADER_LENGTH,
                output = packetBufferCursor,
            )

            if (!encrypted) throw CouldNotEncryptAeadDataException(data)

            // Append 4-byte nonce at the end of the packet
            packetBufferCursor.writeByteArray(nonceBytes)

            // Return view of the complete packet
            if (!packetBufferView.resize(0, packetBufferCursor.cursor)) {
                error("couldn't resize packet buffer view?!")
            }

            packetBufferView
        }
}
