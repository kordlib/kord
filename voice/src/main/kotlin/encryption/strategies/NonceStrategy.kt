package dev.kord.voice.encryption.strategies

import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.udp.RTPPacket

internal const val DEPRECATION_NONCE_STRATEGY = "'NonceStrategy' is no longer a strategy of choosing encryption mode at runtime and the API will be changing in the near future."
internal const val DEPRECATION_UNUSED_STRATEGY = "This nonce strategy is no longer used in any supported encryption mode. $DEPRECATION_NONCE_STRATEGY"
internal const val DEPRECATION_INTERNAL_STRATEGY = "This nonce strategy is only used as an internal implementation. $DEPRECATION_UNUSED_STRATEGY"

/**
 * An [encryption mode, regarding the nonce](https://discord.com/developers/docs/topics/voice-connections#establishing-a-voice-udp-connection-encryption-modes), supported by Discord.
 */
@Deprecated(
    DEPRECATION_NONCE_STRATEGY,
    level = DeprecationLevel.WARNING,
)
public sealed interface NonceStrategy {
    /**
     * The amount of bytes this nonce will take up.
     */
    public val nonceLength: Int

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
}