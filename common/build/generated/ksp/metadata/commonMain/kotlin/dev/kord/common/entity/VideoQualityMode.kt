// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection", "MemberVisibilityCanBePrivate"))

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
            if (this is Unknown) "VideoQualityMode.Unknown(value=$value)"
            else "VideoQualityMode.${this::class.simpleName}"

    /**
     * An unknown [VideoQualityMode].
     *
     * This is used as a fallback for [VideoQualityMode]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
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

        override fun deserialize(decoder: Decoder): VideoQualityMode = from(decoder.decodeInt())
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


        /**
         * Returns an instance of [VideoQualityMode] with [VideoQualityMode.value] equal to the
         * specified [value].
         */
        public fun from(`value`: Int): VideoQualityMode = when (value) {
            1 -> Auto
            2 -> Full
            else -> Unknown(value)
        }
    }
}
