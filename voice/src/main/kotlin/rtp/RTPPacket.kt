package dev.kord.voice.rtp

import okio.Buffer
import kotlin.experimental.and

/**
 * Originally from [this github library](https://github.com/vidtec/rtp-packet/blob/0b54fdeab5666089215b0074c64a6735b8937f8d/src/main/java/org/vidtec/rfc3550/rtp/RTPPacket.java).
 *
 * Ported to Kotlin.
 *
 * Contains modifications to support RTP packets from Discord as they do not adhere to the standard RTP spec.
 *
 * Some changes include
 * - Timestamps are stored as UInt (4 bytes) not long.
 * - Sequences are stored as UShort (2 bytes) not long.
 * - Payload Types are stored as UByte (1 byte) not short.
 * - SSRCs are stored as UInt (4 bytes) not long.
 * - And most notably, the extension header is not exactly after the RTP header. Discord instead encrypts it along with the payload...
 */
@Suppress("ArrayInDataClass")
@OptIn(ExperimentalUnsignedTypes::class)
internal data class RTPPacket(
    val paddingBytes: UByte,
    val payloadType: Byte,
    val sequence: UShort,
    val timestamp: UInt,
    val ssrc: UInt,
    val csrcIdentifiers: UIntArray,
    val hasMarker: Boolean,
    val hasExtension: Boolean,
    val payload: ByteArray,
) {
    companion object {
        const val VERSION = 2

        fun fromBytes(buffer: Buffer) = with(buffer) {
            val initialLength = size

            require(initialLength > 13) { "packet too short" }

            /*
             * first byte | bit table
             * 0 = version
             * 1 = padding bit
             * 2 = extension bit
             * 3-7 = csrc count
             */
            val paddingBytes: UByte
            val hasExtension: Boolean
            val csrcCount: Byte
            with(readByte()) {
                require(((toInt() and 0xC0) == VERSION shl 6)) { "invalid version" }

                paddingBytes = if (((toInt() and 0x20) == 0x20)) buffer[initialLength - 1].toUByte() else 0u;
                hasExtension = (toInt() and 0x10) == 0x10
                csrcCount = toByte() and 0x0F
            }

            /*
             * second byte | bit table
             * 0 = marker
             * 1-7 = payload type
             */
            val marker: Boolean
            val payloadType: Byte
            with(readByte()) {
                marker = (this and 0x80.toByte()) == 0x80.toByte()
                payloadType = this and 0x7F
            }

            val sequence = readShort().toUShort()
            val timestamp = readInt().toUInt()
            val ssrc = readInt().toUInt()

            // each csrc takes up 4 bytes, plus more data is required
            require(size > csrcCount * 4 + 1) { "packet too short" }
            val csrcIdentifiers = UIntArray(csrcCount.toInt()) { readInt().toUInt() }

            val payload = readByteArray().apply { copyOfRange(0, size - paddingBytes.toInt()) }

            RTPPacket(
                paddingBytes,
                payloadType,
                sequence,
                timestamp,
                ssrc,
                csrcIdentifiers,
                marker,
                hasExtension,
                payload
            )
        }
    }

    init {
        require(payloadType < 127 || payloadType > 0) { "invalid payload type" }
        require(payload.isNotEmpty()) { "invalid payload" }
    }

    private val header = with(Buffer()) {
        val hasPadding = if (paddingBytes > 0u) 0x20 else 0x00
        val hasExtension = if (hasExtension) 0x10 else 0x0
        writeByte((VERSION shl 6) or (hasPadding) or (hasExtension) or 0)
        writeByte(payloadType.toInt())
        writeShort(sequence.toInt())
        writeInt(timestamp.toInt())
        writeInt(ssrc.toInt())

        readByteArray()
    }

    fun asByteArray() = with(Buffer()) {
        write(header)
        write(payload)

        if (paddingBytes > 0u) {
            write(ByteArray(paddingBytes.toInt() - 1) { 0x0 })
            writeByte(paddingBytes.toInt())
        }

        buffer.readByteArray()
    }

    class Builder(
        var ssrc: UInt,
        var timestamp: UInt,
        var sequence: UShort,
        var payloadType: Byte,
        var payload: ByteArray
    ) {
        var marker: Boolean = false
        var paddingBytes: UByte = 0u
        var hasExtension: Boolean = false
        var csrcIdentifiers: UIntArray = uintArrayOf()

        fun build() = RTPPacket(
            paddingBytes,
            payloadType,
            sequence,
            timestamp,
            ssrc,
            csrcIdentifiers,
            marker,
            hasExtension,
            payload
        )
    }
}

internal fun RTPPacket(
    ssrc: UInt,
    timestamp: UInt,
    sequence: UShort,
    payloadType: Byte,
    payload: ByteArray,
    builder: RTPPacket.Builder.() -> Unit = {}
) = RTPPacket.Builder(ssrc, timestamp, sequence, payloadType, payload).apply(builder).build()