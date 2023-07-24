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
 * See [ForumLayoutType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#channel-object-forum-layout-types).
 */
@Serializable(with = ForumLayoutType.Serializer::class)
public sealed class ForumLayoutType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ForumLayoutType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            "ForumLayoutType.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [ForumLayoutType].
     *
     * This is used as a fallback for [ForumLayoutType]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: Int,
    ) : ForumLayoutType(value)

    /**
     * No default has been set for forum channel.
     */
    public object NotSet : ForumLayoutType(0)

    /**
     * Display posts as a list.
     */
    public object ListView : ForumLayoutType(1)

    /**
     * Display posts as a collection of tiles.
     */
    public object GalleryView : ForumLayoutType(2)

    internal object Serializer : KSerializer<ForumLayoutType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ForumLayoutType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ForumLayoutType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): ForumLayoutType =
                when (val value = decoder.decodeInt()) {
            0 -> NotSet
            1 -> ListView
            2 -> GalleryView
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [ForumLayoutType]s.
         */
        public val entries: List<ForumLayoutType> by lazy(mode = PUBLICATION) {
            listOf(
                NotSet,
                ListView,
                GalleryView,
            )
        }

    }
}
