// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import dev.kord.common.`annotation`.KordPreview
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
 * See [MemberVerificationFormFieldType]s in the [Discord Developer Documentation](https://docs.discord.food/resources/guild#member-verification-form-field-type).
 */
@Serializable(with = MemberVerificationFormFieldType.Serializer::class)
@KordPreview
public sealed class MemberVerificationFormFieldType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other || (other is MemberVerificationFormFieldType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "MemberVerificationFormFieldType.Unknown(value=$value)" else "MemberVerificationFormFieldType.${this::class.simpleName}"

    /**
     * An unknown [MemberVerificationFormFieldType].
     *
     * This is used as a fallback for [MemberVerificationFormFieldType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: String,
    ) : MemberVerificationFormFieldType(value)

    /**
     * User must agree to the guild rules
     */
    public object Terms : MemberVerificationFormFieldType("TERMS")

    /**
     * User must respond with a short answer (max 150 characters)
     */
    public object TextInput : MemberVerificationFormFieldType("TEXT_INPUT")

    /**
     * User must respond with a paragraph (max 1000 characters
     */
    public object Paragraph : MemberVerificationFormFieldType("PARAGRAPH")

    /**
     * User must select one of the provided choices
     */
    public object MultipleChoice : MemberVerificationFormFieldType("MULTIPLE_CHOICE")

    internal object Serializer : KSerializer<MemberVerificationFormFieldType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.MemberVerificationFormFieldType", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: MemberVerificationFormFieldType) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): MemberVerificationFormFieldType = from(decoder.decodeString())
    }

    public companion object {
        /**
         * A [List] of all known [MemberVerificationFormFieldType]s.
         */
        public val entries: List<MemberVerificationFormFieldType> by lazy(mode = PUBLICATION) {
            listOf(
                Terms,
                TextInput,
                Paragraph,
                MultipleChoice,
            )
        }

        /**
         * Returns an instance of [MemberVerificationFormFieldType] with [MemberVerificationFormFieldType.value] equal to the specified [value].
         */
        public fun from(`value`: String): MemberVerificationFormFieldType = when (value) {
            "TERMS" -> Terms
            "TEXT_INPUT" -> TextInput
            "PARAGRAPH" -> Paragraph
            "MULTIPLE_CHOICE" -> MultipleChoice
            else -> Unknown(value)
        }
    }
}
