// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

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
 * See [VerificationLevel]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-object-verification-level).
 */
@Serializable(with = VerificationLevel.Serializer::class)
public sealed class VerificationLevel(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is VerificationLevel && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "VerificationLevel.Unknown(value=$value)"
            else "VerificationLevel.${this::class.simpleName}"

    /**
     * An unknown [VerificationLevel].
     *
     * This is used as a fallback for [VerificationLevel]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : VerificationLevel(value)

    /**
     * Unrestricted.
     */
    public object None : VerificationLevel(0)

    /**
     * Must have verified email on account.
     */
    public object Low : VerificationLevel(1)

    /**
     * Must be registered on Discord for longer than 5 minutes.
     */
    public object Medium : VerificationLevel(2)

    /**
     * Must be a member of the server for longer than 10 minutes.
     */
    public object High : VerificationLevel(3)

    /**
     * Must have a verified phone number.
     */
    public object VeryHigh : VerificationLevel(4)

    internal object Serializer : KSerializer<VerificationLevel> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.VerificationLevel",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: VerificationLevel) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): VerificationLevel = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [VerificationLevel]s.
         */
        public val entries: List<VerificationLevel> by lazy(mode = PUBLICATION) {
            listOf(
                None,
                Low,
                Medium,
                High,
                VeryHigh,
            )
        }

        /**
         * Returns an instance of [VerificationLevel] with [VerificationLevel.value] equal to the
         * specified [value].
         */
        public fun from(`value`: Int): VerificationLevel = when (value) {
            0 -> None
            1 -> Low
            2 -> Medium
            3 -> High
            4 -> VeryHigh
            else -> Unknown(value)
        }
    }
}
