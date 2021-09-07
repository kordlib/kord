package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.XSalsa20Poly1305Codec
import io.ktor.utils.io.core.*

@OptIn(ExperimentalUnsignedTypes::class)
@KordVoice
sealed class AudioPacket(
    val sequence: UShort,
    val timestamp: UInt,
    val ssrc: UInt,
    val data: ByteArray
) {
    @OptIn(ExperimentalIoApi::class)
    val header = BytePacketBuilder().apply {
        writeByte(RTP_TYPE)
        writeByte(RTP_VERSION)
        writeUShort(sequence)
        writeUInt(timestamp)
        writeUInt(ssrc)
    }.build().readBytes()

    class EncryptedPacket(
        sequence: UShort,
        timestamp: UInt,
        ssrc: UInt,
        encryptedData: ByteArray
    ) : AudioPacket(sequence, timestamp, ssrc, encryptedData) {
        fun decrypt(key: ByteArray): DecryptedPacket {
            val nonce = ByteArray(NONCE_LENGTH)
            header.copyInto(nonce, 0, 0, RTP_HEADER_LENGTH)
            val decrypted = XSalsa20Poly1305Codec.decrypt(data, key, nonce) ?: error("couldn't decrypt audio data")
            return DecryptedPacket(sequence, timestamp, ssrc, decrypted)
        }
    }

    fun asByteReadPacket() = BytePacketBuilder().also {
        it.writeFully(header)
        it.writeFully(data)
    }.build()

    class DecryptedPacket(
        sequence: UShort,
        timestamp: UInt,
        ssrc: UInt,
        decryptedData: ByteArray
    ) : AudioPacket(sequence, timestamp, ssrc, decryptedData) {
        fun encrypt(key: ByteArray): EncryptedPacket {
            val nonce = ByteArray(NONCE_LENGTH)
            header.copyInto(nonce, 0, 0, RTP_HEADER_LENGTH)
            val encrypted = XSalsa20Poly1305Codec.encrypt(data, key, nonce)
            return EncryptedPacket(sequence, timestamp, ssrc, encrypted)
        }
    }

    companion object {
        private const val RTP_TYPE: Byte = 0x90.toByte()
        private const val RTP_VERSION = 0x78.toByte()
        private const val NONCE_LENGTH = 24
        private const val RTP_HEADER_LENGTH = 12

        fun encryptedFrom(data: ByteReadPacket): EncryptedPacket? {
            try {
                with(data) {
                    require(readByte() == RTP_TYPE)
                    require(readByte() == RTP_VERSION)
                    val sequence = readUShort()
                    val timestamp = readUInt()
                    val ssrc = readUInt()
                    val encryptedData = readBytes()

                    return EncryptedPacket(sequence, timestamp, ssrc, encryptedData)
                }
            } catch (e: Exception) {
                return null
            }
        }
    }
}