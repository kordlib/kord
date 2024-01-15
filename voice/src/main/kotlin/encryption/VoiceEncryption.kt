package dev.kord.voice.encryption

import com.iwebpp.crypto.TweetNaclFast
import dev.kord.voice.EncryptionMode
import dev.kord.voice.encryption.strategies.LiteNonceStrategy
import dev.kord.voice.encryption.strategies.NonceStrategy
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view
import dev.kord.voice.udp.RTPPacket
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

public sealed interface VoiceEncryption {
    public val nonceLength: Int

    public val mode: EncryptionMode

    /**
     * Whether this encryption mode supports decryption.
     */
    public val supportsDecryption: Boolean

    public fun createBox(key: ByteArray): Box

    public fun createUnbox(key: ByteArray): Unbox

    public data class XSalsaPoly1305(
        public val nonceStrategyFactory: NonceStrategy.Factory = LiteNonceStrategy,
    ) : VoiceEncryption {
        override val supportsDecryption: Boolean get() = true
        override val mode: EncryptionMode get() = nonceStrategyFactory.mode
        override val nonceLength: Int get() = 24

        override fun createBox(key: ByteArray): Box = object : Box {
            private val codec: XSalsa20Poly1305Codec = XSalsa20Poly1305Codec(key)
            private val nonceStrategy: NonceStrategy = nonceStrategyFactory.create()

            override val overhead: Int
                get() = TweetNaclFast.SecretBox.boxzerobytesLength + nonceStrategyFactory.length

            override fun encrypt(src: ByteArray, nonce: ByteArray, dst: MutableByteArrayCursor): Boolean {
                return codec.encrypt(src, 0, src.size, nonce, dst)
            }

            override fun generateNonce(header: () -> ByteArrayView): ByteArrayView {
                return nonceStrategy.generate(header)
            }

            override fun appendNonce(nonce: ByteArrayView, dst: MutableByteArrayCursor) {
                nonceStrategy.append(nonce, dst)
            }
        }

        override fun createUnbox(key: ByteArray): Unbox = object : Unbox {
            private val codec: XSalsa20Poly1305Codec = XSalsa20Poly1305Codec(key)
            private val nonceStrategy: NonceStrategy = nonceStrategyFactory.create()

            override fun decrypt(
                src: ByteArray,
                srcOff: Int,
                srcLen: Int,
                nonce: ByteArray,
                dst: MutableByteArrayCursor,
            ): Boolean = codec.decrypt(src, srcOff, srcLen, nonce, dst)

            override fun getNonce(packet: RTPPacket): ByteArrayView = nonceStrategy.strip(packet)
        }
    }

    public data object AeadAes256Gcm : VoiceEncryption {
        private const val AUTH_TAG_LEN = 16
        private const val NONCE_LEN = 4
        private const val IV_LEN = 12

        override val supportsDecryption: Boolean get() = false
        override val mode: EncryptionMode get() = EncryptionMode.AeadAes256Gcm
        override val nonceLength: Int get() = 4

        override fun createBox(key: ByteArray): Box = object : Box {
            private val iv = ByteArray(IV_LEN)
            private val ivCursor = iv.mutableCursor()

            private var nonce = 0u
            private val nonceBuffer: ByteArray = ByteArray(NONCE_LEN)
            private val nonceCursor = nonceBuffer.mutableCursor()
            private val nonceView = nonceBuffer.view()

            val secretKey = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")

            override val overhead: Int
                get() = AUTH_TAG_LEN + NONCE_LEN

            override fun encrypt(src: ByteArray, nonce: ByteArray, dst: MutableByteArrayCursor): Boolean {
                iv.fill(0)
                ivCursor.reset()
                ivCursor.writeByteArray(nonce)

                cipher.init(
                    Cipher.ENCRYPT_MODE, secretKey,
                    GCMParameterSpec(AUTH_TAG_LEN * 8, iv, 0, IV_LEN)
                )
                cipher.updateAAD(dst.data.copyOfRange(0, dst.cursor))
                dst.cursor += cipher.doFinal(src, 0, src.size, dst.data, dst.cursor)

                return true
            }

            override fun appendNonce(nonce: ByteArrayView, dst: MutableByteArrayCursor) {
                dst.writeByteView(nonce)
            }

            override fun generateNonce(header: () -> ByteArrayView): ByteArrayView {
                nonceCursor.reset()
                nonceCursor.writeInt(nonce++.toInt())
                return nonceView
            }
        }

        override fun createUnbox(key: ByteArray): Unbox {
            throw UnsupportedOperationException()
        }
    }

    public interface Box {
        public val overhead: Int

        public fun encrypt(src: ByteArray, nonce: ByteArray, dst: MutableByteArrayCursor): Boolean

        public fun generateNonce(header: () -> ByteArrayView): ByteArrayView

        public fun appendNonce(nonce: ByteArrayView, dst: MutableByteArrayCursor)
    }

    public interface Unbox {
        /**
         * Decrypt the packet.
         */
        public fun decrypt(
            src: ByteArray,
            srcOff: Int,
            srcLen: Int,
            nonce: ByteArray,
            dst: MutableByteArrayCursor,
        ): Boolean

        /**
         * Strip the nonce from the [RTP packet][packet].
         *
         * @return the nonce.
         */
        public fun getNonce(packet: RTPPacket): ByteArrayView
    }
}