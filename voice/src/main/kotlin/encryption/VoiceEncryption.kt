package dev.kord.voice.encryption

import dev.kord.voice.EncryptionMode
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.udp.RTPPacket

// TODO: improve kdoc
// TODO: improve nonce methods to reduce unnecessary byte copying.
// TODO: commonize nonce methods.

public interface VoiceEncryption {
    public val nonceLength: Int

    /**
     * The [EncryptionMode] this encryption strategy fulfills.
     */
    public val mode: EncryptionMode

    /**
     * Creates a [Box] instance for the specified [key bytes][key]
     *
     * @param key A byte array containing the 256-bit key material.
     */
    public fun createBox(key: ByteArray): Box

    /**
     * Creates a [Box] instance for the specified [key bytes][key]
     *
     * @param key A byte array containing the 256-bit key material.
     */
    public fun createUnbox(key: ByteArray): Unbox

    /**
     * A common interface for (un)boxing voice packets.
     */
    public sealed interface Method {

        public fun apply(src: ByteArrayView, nonce: ByteArray, dst: MutableByteArrayCursor): Boolean
    }

    public interface Box : Method {
        /**
         * The number of extra bytes this [Box] generates.
         */
        public val overhead: Int

        /**
         *
         */
        public fun generateNonce(header: () -> ByteArrayView): ByteArrayView

        /**
         * Appends the specified [nonce buffer][nonce] to the [destination cursor][dst]
         */
        public fun appendNonce(nonce: ByteArrayView, dst: MutableByteArrayCursor)
    }

    public interface Unbox : Method {

        /**
         * Returns the nonce from the given [RTP packet][packet].
         *
         * @return the nonce.
         */
        public fun getNonce(packet: RTPPacket): ByteArrayView
    }
}
