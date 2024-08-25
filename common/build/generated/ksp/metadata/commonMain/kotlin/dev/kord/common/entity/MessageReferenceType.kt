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
 * See [MessageReferenceType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/message#message-reference-structure).
 */
@Serializable(with = MessageReferenceType.Serializer::class)
public sealed class MessageReferenceType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is MessageReferenceType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "MessageReferenceType.Unknown(value=$value)"
            else "MessageReferenceType.${this::class.simpleName}"

    /**
     * An unknown [MessageReferenceType].
     *
     * This is used as a fallback for [MessageReferenceType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : MessageReferenceType(value)

    /**
     * A standard reference used by replies.
     */
    public object Default : MessageReferenceType(0)

    /**
     * Reference used to point to a message at a point in time.
     */
    public object Forward : MessageReferenceType(1)

    internal object Serializer : KSerializer<MessageReferenceType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.MessageReferenceType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: MessageReferenceType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): MessageReferenceType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [MessageReferenceType]s.
         */
        public val entries: List<MessageReferenceType> by lazy(mode = PUBLICATION) {
            listOf(
                Default,
                Forward,
            )
        }

        /**
         * Returns an instance of [MessageReferenceType] with [MessageReferenceType.value] equal to
         * the specified [value].
         */
        public fun from(`value`: Int): MessageReferenceType = when (value) {
            0 -> Default
            1 -> Forward
            else -> Unknown(value)
        }
    }
}
