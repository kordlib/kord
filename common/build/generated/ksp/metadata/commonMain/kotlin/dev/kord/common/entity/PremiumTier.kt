// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * See [PremiumTier]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-object-premium-tier).
 */
@Serializable(with = PremiumTier.Serializer::class)
public sealed class PremiumTier(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is PremiumTier && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = "PremiumTier.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [PremiumTier].
     *
     * This is used as a fallback for [PremiumTier]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: Int,
    ) : PremiumTier(value)

    /**
     * Guild has not unlocked any Server Boost perks.
     */
    public object None : PremiumTier(0)

    /**
     * Guild has unlocked Server Boost level 1 perks.
     */
    public object One : PremiumTier(1)

    /**
     * Guild has unlocked Server Boost level 2 perks.
     */
    public object Two : PremiumTier(2)

    /**
     * Guild has unlocked Server Boost level 3 perks.
     */
    public object Three : PremiumTier(3)

    internal object Serializer : KSerializer<PremiumTier> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.PremiumTier", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: PremiumTier) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): PremiumTier =
                when (val value = decoder.decodeInt()) {
            0 -> None
            1 -> One
            2 -> Two
            3 -> Three
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [PremiumTier]s.
         */
        public val entries: List<PremiumTier> by lazy(mode = PUBLICATION) {
            listOf(
                None,
                One,
                Two,
                Three,
            )
        }

    }
}
