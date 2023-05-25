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
 * See [MessageStickerType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/sticker#sticker-object-sticker-format-types).
 */
@Serializable(with = MessageStickerType.Serializer::class)
public sealed class MessageStickerType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is MessageStickerType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            "MessageStickerType.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [MessageStickerType].
     *
     * This is used as a fallback for [MessageStickerType]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: Int,
    ) : MessageStickerType(value)

    public object PNG : MessageStickerType(1)

    public object APNG : MessageStickerType(2)

    public object LOTTIE : MessageStickerType(3)

    public object GIF : MessageStickerType(4)

    internal object Serializer : KSerializer<MessageStickerType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.MessageStickerType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: MessageStickerType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): MessageStickerType =
                when (val value = decoder.decodeInt()) {
            1 -> PNG
            2 -> APNG
            3 -> LOTTIE
            4 -> GIF
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [MessageStickerType]s.
         */
        public val entries: List<MessageStickerType> by lazy(mode = PUBLICATION) {
            listOf(
                PNG,
                APNG,
                LOTTIE,
                GIF,
            )
        }

    }
}
