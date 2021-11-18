package dev.kord.voice.encryption.strategies

import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.view
import dev.kord.voice.udp.RTPPacket
import kotlin.random.Random

private const val SUFFIX_NONCE_LENGTH = 24

public class SuffixNonceStrategy : NonceStrategy {
    override val nonceLength: Int = SUFFIX_NONCE_LENGTH

    private val nonceBuffer: ByteArray = ByteArray(SUFFIX_NONCE_LENGTH)
    private val nonceView = nonceBuffer.view()

    override fun strip(packet: RTPPacket): ByteArrayView {
        return with(packet.payload) {
            val nonce = view(dataEnd - SUFFIX_NONCE_LENGTH, dataEnd)!!
            resize(dataStart, dataEnd - SUFFIX_NONCE_LENGTH)
            nonce
        }
    }

    override fun generate(header: () -> ByteArrayView): ByteArrayView {
        Random.Default.nextBytes(nonceBuffer)
        return nonceView
    }

    override fun append(nonce: ByteArrayView, cursor: MutableByteArrayCursor) {
        cursor.writeByteView(nonce)
    }
}