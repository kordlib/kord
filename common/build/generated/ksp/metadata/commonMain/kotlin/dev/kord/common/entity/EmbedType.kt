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
 * See [EmbedType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#embed-object-embed-types).
 */
@Serializable(with = EmbedType.Serializer::class)
public sealed class EmbedType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is EmbedType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = "EmbedType.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [EmbedType].
     *
     * This is used as a fallback for [EmbedType]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: String,
    ) : EmbedType(value)

    /**
     * Generic embed rendered from embed attributes.
     */
    public object Rich : EmbedType("rich")

    /**
     * Image embed.
     */
    public object Image : EmbedType("image")

    /**
     * Video embed.
     */
    public object Video : EmbedType("video")

    /**
     * Animated gif image embed rendered as a video embed.
     */
    public object Gifv : EmbedType("gifv")

    /**
     * Article embed.
     */
    public object Article : EmbedType("article")

    /**
     * Link embed.
     */
    public object Link : EmbedType("link")

    internal object Serializer : KSerializer<EmbedType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.EmbedType", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: EmbedType) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): EmbedType =
                when (val value = decoder.decodeString()) {
            "rich" -> Rich
            "image" -> Image
            "video" -> Video
            "gifv" -> Gifv
            "article" -> Article
            "link" -> Link
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [EmbedType]s.
         */
        public val entries: List<EmbedType> by lazy(mode = PUBLICATION) {
            listOf(
                Rich,
                Image,
                Video,
                Gifv,
                Article,
                Link,
            )
        }

    }
}
