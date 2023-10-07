package dev.kord.voice.udp

import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view
import io.ktor.utils.io.core.*
import kotlin.experimental.and

internal const val RTP_HEADER_LENGTH = 12

/**
 * Originally from [this GitHub library](https://github.com/vidtec/rtp-packet/blob/0b54fdeab5666089215b0074c64a6735b8937f8d/src/main/java/org/vidtec/rfc3550/rtp/RTPPacket.java).
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
public data class RTPPacket(
    val paddingBytes: UByte,
    val payloadType: Byte,
    val sequence: UShort,
    val timestamp: UInt,
    val ssrc: UInt,
    val csrcIdentifiers: UIntArray,
    val hasMarker: Boolean,
    val hasExtension: Boolean,
    val payload: ByteArrayView,
) {
    public companion object {
        internal const val VERSION = 2

        public fun fromPacket(packet: ByteReadPacket): RTPPacket? = with(packet) base@{
            if (remaining <= 13) return@base null

            /*
             * first byte | bit table
             * 0 = version
             * 1 = padding bit
             * 2 = extension bit
             * 3-7 = csrc count
             */
            val hasPadding: Boolean
            val hasExtension: Boolean
            val csrcCount: Byte
            with(readByte()) {
                // invalid rtp version
                if ((toInt() and 0xC0) != VERSION shl 6) return@base null

                hasPadding = (toInt() and 0x20) == 0x20
                hasExtension = (toInt() and 0x10) == 0x10
                csrcCount = this and 0x0F
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
            if (remaining <= csrcCount * 4 + 1) return@base null
            val csrcIdentifiers = UIntArray(csrcCount.toInt()) { readUInt() }

            val payload = readBytes().view()

            val paddingBytes = if (hasPadding) { payload[payload.viewSize - 1] } else 0

            payload.resize(end = payload.dataStart + payload.viewSize - paddingBytes)

            RTPPacket(
                paddingBytes.toUByte(),
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

    public fun clone(): RTPPacket {
        return RTPPacket(
            paddingBytes,
            payloadType,
            sequence,
            timestamp,
            ssrc,
            csrcIdentifiers.copyOf(),
            hasMarker,
            hasExtension,
            payload.toByteArray().view()
        )
    }

    public fun writeHeader(): ByteArray {
        val buffer = ByteArray(12)
        writeHeader(buffer.mutableCursor())
        return buffer
    }

    public fun writeHeader(buffer: MutableByteArrayCursor): Unit = with(buffer) {
        resize(cursor + RTP_HEADER_LENGTH)

        val hasPadding = if (paddingBytes > 0u) 0x20 else 0x00
        val hasExtension = if (hasExtension) 0x10 else 0x0
        writeByte(((VERSION shl 6) or (hasPadding) or (hasExtension)).toByte())
        writeByte(payloadType)
        writeShort(sequence.toShort())
        writeInt(timestamp.toInt())
        writeInt(ssrc.toInt())
    }

    public fun asByteArray(): ByteArray {
        val buffer = ByteArray(RTP_HEADER_LENGTH + payload.viewSize + paddingBytes.toInt())
        asByteArrayView(buffer.mutableCursor())
        return buffer
    }

    public fun asByteArrayView(buffer: MutableByteArrayCursor): ByteArrayView = with(buffer) {
        resize(cursor + (RTP_HEADER_LENGTH + payload.viewSize + paddingBytes.toInt()))

        val initial = cursor

        // header
        writeHeader(buffer)

        // payload
        writeByteView(payload)

        if (paddingBytes > 0u) {
            writeByteArray(ByteArray(paddingBytes.toInt() - 1) { 0x0 })
            writeByte(paddingBytes.toByte())
        }

        data.view(initial, cursor)!!
    }

    public class Builder(
        public var ssrc: UInt,
        public var timestamp: UInt,
        public var sequence: UShort,
        public var payloadType: Byte,
        public var payload: ByteArray
    ) {
        public var marker: Boolean = false
        public var paddingBytes: UByte = 0u
        public var hasExtension: Boolean = false
        public var csrcIdentifiers: UIntArray = uintArrayOf()

        public fun build(): RTPPacket = RTPPacket(
            paddingBytes,
            payloadType,
            sequence,
            timestamp,
            ssrc,
            csrcIdentifiers,
            marker,
            hasExtension,
            payload.view()
        )
    }
}

public fun RTPPacket(
    ssrc: UInt,
    timestamp: UInt,
    sequence: UShort,
    payloadType: Byte,
    payload: ByteArray,
    builder: RTPPacket.Builder.() -> Unit = { }
): RTPPacket = RTPPacket.Builder(ssrc, timestamp, sequence, payloadType, payload).apply(builder).build()