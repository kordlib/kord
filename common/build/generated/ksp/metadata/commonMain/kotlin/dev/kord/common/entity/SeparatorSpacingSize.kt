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
 * See [SeparatorSpacingSize]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/components/reference#separator-separator-structure).
 */
@Serializable(with = SeparatorSpacingSize.Serializer::class)
public sealed class SeparatorSpacingSize(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is SeparatorSpacingSize && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "SeparatorSpacingSize.Unknown(value=$value)"
            else "SeparatorSpacingSize.${this::class.simpleName}"

    /**
     * An unknown [SeparatorSpacingSize].
     *
     * This is used as a fallback for [SeparatorSpacingSize]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : SeparatorSpacingSize(value)

    public object Small : SeparatorSpacingSize(1)

    public object Large : SeparatorSpacingSize(2)

    internal object Serializer : KSerializer<SeparatorSpacingSize> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.SeparatorSpacingSize",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: SeparatorSpacingSize) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): SeparatorSpacingSize = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [SeparatorSpacingSize]s.
         */
        public val entries: List<SeparatorSpacingSize> by lazy(mode = PUBLICATION) {
            listOf(
                Small,
                Large,
            )
        }

        /**
         * Returns an instance of [SeparatorSpacingSize] with [SeparatorSpacingSize.value] equal to
         * the specified [value].
         */
        public fun from(`value`: Int): SeparatorSpacingSize = when (value) {
            1 -> Small
            2 -> Large
            else -> Unknown(value)
        }
    }
}
