package dev.kord.voice.encryption

import com.iwebpp.crypto.TweetNaclFast
import com.iwebpp.crypto.TweetNaclFast.SecretBox.boxzerobytesLength
import com.iwebpp.crypto.TweetNaclFast.SecretBox.zerobytesLength
import dev.kord.voice.EncryptionMode
import dev.kord.voice.encryption.strategies.LiteNonceStrategy
import dev.kord.voice.encryption.strategies.NonceStrategy
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.udp.RTPPacket

/**
 * An [encryption method][VoiceEncryption] that uses the XSalsa20 stream cipher and Poly1035 hash function.
 */
public data class XSalsa20Poly1305(public val nonceStrategyFactory: NonceStrategy.Factory = LiteNonceStrategy) : VoiceEncryption {
    override val mode: EncryptionMode get() = nonceStrategyFactory.mode

    override val nonceLength: Int get() = 24

    override fun createBox(key: ByteArray): VoiceEncryption.Box = Impl.Box(key, nonceStrategyFactory)

    override fun createUnbox(key: ByteArray): VoiceEncryption.Unbox = Impl.Unbox(key, nonceStrategyFactory)

    public sealed class Impl(protected val nonceStrategyFactory: NonceStrategy.Factory) {
        protected val nonceStrategy: NonceStrategy = nonceStrategyFactory.create()

        // this class is only used internally and is used for encrypting opus packets.
        // we can know the maximum sized buffer required to store any opus packet.
        protected val m: ByteArray = ByteArray(OPUS_MAX_LENGTH + zerobytesLength)
        protected val c: ByteArray = ByteArray(OPUS_MAX_LENGTH + zerobytesLength)

        public class Box(
            private val key: ByteArray,
            nonceStrategyFactory: NonceStrategy.Factory,
        ) : VoiceEncryption.Box, Impl(nonceStrategyFactory) {
            override val overhead: Int
                get() = boxzerobytesLength + nonceStrategyFactory.length

            override fun apply(src: ByteArrayView, dst: MutableByteArrayCursor, aead: ByteArrayView, nonce: ByteArray): Boolean {
                m.fill(0)
                c.fill(0)

                for (i in 0..<src.viewSize)
                    m[i + zerobytesLength] = src[i]

                val srcBufferLength = src.viewSize + zerobytesLength
                if (TweetNaclFast.crypto_secretbox(c, m, srcBufferLength, nonce, key) == 0) {
                    dst.resize(dst.cursor + srcBufferLength - boxzerobytesLength)
                    dst.writeByteArray(c, boxzerobytesLength, srcBufferLength - boxzerobytesLength)
                    return true
                }

                return false
            }

            override fun generateNonce(header: () -> ByteArrayView): ByteArrayView =
                nonceStrategy.generate(header)

            override fun appendNonce(nonce: ByteArrayView, dst: MutableByteArrayCursor): Unit =
                nonceStrategy.append(nonce, dst)
        }

        public class Unbox(
            private val key: ByteArray,
            nonceStrategyFactory: NonceStrategy.Factory,
        ) : VoiceEncryption.Unbox, Impl(nonceStrategyFactory) {
            override fun apply(src: ByteArrayView, dst: MutableByteArrayCursor, aead: ByteArrayView, nonce: ByteArray): Boolean {
                c.fill(0)
                m.fill(0)

                for (i in 0..<src.viewSize)
                    c[i + boxzerobytesLength] = src[i]

                val cipherLength = src.viewSize + TweetNaclFast.Box.boxzerobytesLength

                if (TweetNaclFast.crypto_secretbox_open(m, c, cipherLength, nonce, key) == 0) {
                    dst.resize(dst.cursor + cipherLength - zerobytesLength)
                    dst.writeByteArray(m, TweetNaclFast.Box.zerobytesLength, cipherLength - zerobytesLength)

                    return true
                }

                return false
            }

            override fun getNonce(packet: RTPPacket): ByteArrayView = nonceStrategy.strip(packet)
        }
    }

    public companion object {
        // https://datatracker.ietf.org/doc/html/rfc6716#section-3.2.1
        private const val OPUS_MAX_LENGTH = 1276
    }
}
