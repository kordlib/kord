package dev.kord.voice.encryption.strategies

import com.iwebpp.crypto.TweetNaclFast
import dev.kord.voice.EncryptionMode
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.view
import dev.kord.voice.udp.RTPPacket
import kotlin.random.Random

public class SuffixNonceStrategy : NonceStrategy {
    private val nonceBuffer: ByteArray = ByteArray(length)
    private val nonceView = nonceBuffer.view()

    override fun strip(packet: RTPPacket): ByteArrayView {
        return with(packet.payload) {
            val nonce = view(dataEnd - length, dataEnd)!!
            resize(dataStart, dataEnd - length)
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

    public companion object Factory : NonceStrategy.Factory {
        override val length: Int = TweetNaclFast.SecretBox.nonceLength

        override val mode: EncryptionMode get() = EncryptionMode.XSalsa20Poly1305Suffix

        override fun create(): NonceStrategy = SuffixNonceStrategy()
    }
}
