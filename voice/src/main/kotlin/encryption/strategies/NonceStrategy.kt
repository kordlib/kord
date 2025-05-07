package dev.kord.voice.encryption.strategies

import dev.kord.voice.EncryptionMode
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.udp.RTPPacket

/**
 * An [encryption mode, regarding the nonce](https://discord.com/developers/docs/topics/voice-connections#establishing-a-voice-udp-connection-encryption-modes), supported by Discord.
 */
public sealed interface NonceStrategy {
    /**
     * Reads the nonce from this [packet] (also removes it if it resides in the payload), and returns a [ByteArrayView] of it.
     */
    public fun strip(packet: RTPPacket): ByteArrayView

    /**
     * Generates a nonce, may use the provided information.
     */
    public fun generate(header: () -> ByteArrayView): ByteArrayView

    /**
     * Writes the [nonce] to [cursor] in the correct relative position.
     */
    public fun append(nonce: ByteArrayView, cursor: MutableByteArrayCursor)

    public interface Factory {
        /**
         * The [EncryptionMode] this nonce strategy is for.
         */
        public val mode: EncryptionMode

        /**
         * The amount of bytes this nonce will take up.
         */
        public val length: Int

        public fun create(): NonceStrategy
    }
}