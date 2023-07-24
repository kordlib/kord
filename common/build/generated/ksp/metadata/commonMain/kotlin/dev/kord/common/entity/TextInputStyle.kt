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
 * Style of a [text input][dev.kord.common.entity.ComponentType.TextInput].
 *
 * See [TextInputStyle]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/message-components#text-inputs-text-input-styles).
 */
@Serializable(with = TextInputStyle.Serializer::class)
public sealed class TextInputStyle(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is TextInputStyle && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = "TextInputStyle.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [TextInputStyle].
     *
     * This is used as a fallback for [TextInputStyle]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: Int,
    ) : TextInputStyle(value)

    /**
     * A single-line input.
     */
    public object Short : TextInputStyle(1)

    /**
     * A multi-line input.
     */
    public object Paragraph : TextInputStyle(2)

    internal object Serializer : KSerializer<TextInputStyle> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.TextInputStyle",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: TextInputStyle) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): TextInputStyle =
                when (val value = decoder.decodeInt()) {
            1 -> Short
            2 -> Paragraph
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [TextInputStyle]s.
         */
        public val entries: List<TextInputStyle> by lazy(mode = PUBLICATION) {
            listOf(
                Short,
                Paragraph,
            )
        }

    }
}
