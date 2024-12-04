package dev.kord.voice.encryption.strategies

import dev.kord.voice.XSalsa20_CLASS_DEPRECATION
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view
import dev.kord.voice.udp.RTPPacket
import dev.kord.voice.udp.RTP_HEADER_LENGTH

@Deprecated(
    "'NormalNonceStrategy' is only used for XSalsa20 Poly1305 encryption. $XSalsa20_CLASS_DEPRECATION",
    level = DeprecationLevel.WARNING,
)
public class NormalNonceStrategy : @Suppress("DEPRECATION") NonceStrategy {
    // the nonce is already a part of the rtp header, which means this will take up no extra space.
    override val nonceLength: Int = 0

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
}