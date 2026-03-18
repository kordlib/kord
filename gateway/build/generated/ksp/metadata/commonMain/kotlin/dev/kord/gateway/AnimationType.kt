// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.gateway

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 *
 *
 * See [AnimationType]s in the [Discord Developer Documentation](https://discord.com/developers/docs/events/gateway-events#voice-channel-effect-send-animation-types).
 */
@Serializable(with = AnimationType.Serializer::class)
public sealed class AnimationType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other || (other is AnimationType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "AnimationType.Unknown(value=$value)" else "AnimationType.${this::class.simpleName}"

    /**
     * An unknown [AnimationType].
     *
     * This is used as a fallback for [AnimationType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : AnimationType(value)

    public object Basic : AnimationType(0)

    public object Premium : AnimationType(1)

    internal object Serializer : KSerializer<AnimationType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.gateway.AnimationType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: AnimationType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): AnimationType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [AnimationType]s.
         */
        public val entries: List<AnimationType> by lazy(mode = PUBLICATION) {
            listOf(
                Basic,
                Premium,
            )
        }

        /**
         * Returns an instance of [AnimationType] with [AnimationType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): AnimationType = when (value) {
            0 -> Basic
            1 -> Premium
            else -> Unknown(value)
        }
    }
}
