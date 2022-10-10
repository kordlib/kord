// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor.kt, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Int
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.ReplaceWith
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Set
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
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is MessageStickerType && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    public final override fun toString(): String =
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

    internal object Serializer : KSerializer<MessageStickerType> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.MessageStickerType",
                PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder, `value`: MessageStickerType) =
                encoder.encodeInt(value.value)

        public override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> PNG
            2 -> APNG
            3 -> LOTTIE
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
            )
        }


        @Deprecated(
            message = "Renamed to 'entries'.",
            replaceWith = ReplaceWith(expression = "this.entries", imports = arrayOf()),
        )
        public val values: Set<MessageStickerType>
            get() = entries.toSet()
    }
}
