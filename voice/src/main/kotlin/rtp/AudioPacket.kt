package dev.kord.voice.rtp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.XSalsa20Poly1305Codec
import io.ktor.utils.io.core.*
import okio.Buffer

/**
 * A guestimated list of known Discord RTP payloads.
 */
sealed class PayloadType(val value: Byte) {
    object Alive: PayloadType(0x37.toByte())
    object Audio: PayloadType(0x78.toByte())
    class Unknown(value: Byte): PayloadType(value)

    companion object {
        fun from(value: Byte) = when(value) {
            0x37.toByte() -> Alive
            0x78.toByte() -> Audio
            else -> Unknown(value)
        }
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
@KordVoice
sealed class AudioPacket private constructor(internal val packet: RTPPacket) {
    val sequence: UShort = packet.sequence
    val timestamp: UInt = packet.timestamp
    val ssrc: UInt = packet.ssrc
    val payload: ByteArray = packet.payload
    val payloadType: PayloadType = PayloadType.from(packet.payloadType)

    class EncryptedPacket internal constructor(
        packet: RTPPacket,
    ) : AudioPacket(packet) {
        fun decrypt(key: ByteArray): DecryptedPacket {
            val nonce = ByteArray(NONCE_LENGTH)
            packet.asByteArray().copyInto(nonce, 0, 0, RTP_HEADER_LENGTH)
            val decrypted = XSalsa20Poly1305Codec.decrypt(payload, key, nonce) ?: error("fuck me")
            return DecryptedPacket(packet.copy(payload = decrypted))
        }
    }

    fun asByteReadPacket() = ByteReadPacket(packet.asByteArray())

    class DecryptedPacket internal constructor(
        packet: RTPPacket,
    ) : AudioPacket(packet) {
        fun encrypt(key: ByteArray): EncryptedPacket {
            val nonce = ByteArray(NONCE_LENGTH)
            packet.asByteArray().copyInto(nonce, 0, 0, RTP_HEADER_LENGTH)
            val encrypted = XSalsa20Poly1305Codec.encrypt(payload, key, nonce)
            return EncryptedPacket(packet.copy(payload = encrypted))
        }

        @PublishedApi
        internal companion object {
            fun create(
                sequence: UShort,
                timestamp: UInt,
                ssrc: UInt,
                decryptedData: ByteArray
            ): DecryptedPacket = DecryptedPacket(
                RTPPacket(ssrc, timestamp, sequence, PayloadType.Audio.value, decryptedData)
            )
        }
    }

    companion object {
        private const val NONCE_LENGTH = 24
        private const val RTP_HEADER_LENGTH = 12

        @OptIn(ExperimentalStdlibApi::class)
        fun encryptedFrom(data: ByteReadPacket): EncryptedPacket? {
            return try {
                val packet = RTPPacket.fromBytes(Buffer().write(data.copy().readBytes()))
                EncryptedPacket(packet)
            } catch(e: Exception) {
                null
            }
        }
    }
}