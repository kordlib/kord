package dev.kord.voice.encryption.strategies

import dev.kord.voice.EncryptionMode
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view
import dev.kord.voice.udp.RTPPacket
import dev.kord.voice.udp.RTP_HEADER_LENGTH

public class NormalNonceStrategy : NonceStrategy {
    private val rtpHeaderBuffer = ByteArray(RTP_HEADER_LENGTH)
    private val rtpHeaderCursor = rtpHeaderBuffer.mutableCursor()
    private val rtpHeaderView = rtpHeaderBuffer.view()

    override fun strip(packet: RTPPacket): ByteArrayView {
        rtpHeaderCursor.reset()
        packet.writeHeader(rtpHeaderCursor)
        return rtpHeaderView
    }

    override fun generate(header: () -> ByteArrayView): ByteArrayView {
        return header()
    }

    override fun append(nonce: ByteArrayView, cursor: MutableByteArrayCursor) {
        /* the nonce is the rtp header which means this should do nothing */
    }

    public companion object Factory : NonceStrategy.Factory {
        override val length: Int = 0

        override val mode: EncryptionMode get() = EncryptionMode.XSalsa20Poly1305

        override fun create(): NonceStrategy = NormalNonceStrategy()
    }
}