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
 * See [VideoQualityMode]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#channel-object-video-quality-modes).
 */
@Serializable(with = VideoQualityMode.Serializer::class)
public sealed class VideoQualityMode(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is VideoQualityMode && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            "VideoQualityMode.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [VideoQualityMode].
     *
     * This is used as a fallback for [VideoQualityMode]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: Int,
    ) : VideoQualityMode(value)

    /**
     * Discord chooses the quality for optimal performance.
     */
    public object Auto : VideoQualityMode(1)

    /**
     * 720p.
     */
    public object Full : VideoQualityMode(2)

    internal object Serializer : KSerializer<VideoQualityMode> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.VideoQualityMode",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: VideoQualityMode) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): VideoQualityMode =
                when (val value = decoder.decodeInt()) {
            1 -> Auto
            2 -> Full
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [VideoQualityMode]s.
         */
        public val entries: List<VideoQualityMode> by lazy(mode = PUBLICATION) {
            listOf(
                Auto,
                Full,
            )
        }

    }
}
