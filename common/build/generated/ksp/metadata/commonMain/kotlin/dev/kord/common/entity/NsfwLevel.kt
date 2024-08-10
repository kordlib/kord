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
 * See [NsfwLevel]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-object-guild-nsfw-level).
 */
@Serializable(with = NsfwLevel.Serializer::class)
public sealed class NsfwLevel(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is NsfwLevel && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "NsfwLevel.Unknown(value=$value)"
            else "NsfwLevel.${this::class.simpleName}"

    /**
     * An unknown [NsfwLevel].
     *
     * This is used as a fallback for [NsfwLevel]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : NsfwLevel(value)

    public object Default : NsfwLevel(0)

    public object Explicit : NsfwLevel(1)

    public object Safe : NsfwLevel(2)

    public object AgeRestricted : NsfwLevel(3)

    internal object Serializer : KSerializer<NsfwLevel> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.NsfwLevel", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: NsfwLevel) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): NsfwLevel = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [NsfwLevel]s.
         */
        public val entries: List<NsfwLevel> by lazy(mode = PUBLICATION) {
            listOf(
                Default,
                Explicit,
                Safe,
                AgeRestricted,
            )
        }

        /**
         * Returns an instance of [NsfwLevel] with [NsfwLevel.value] equal to the specified [value].
         */
        public fun from(`value`: Int): NsfwLevel = when (value) {
            0 -> Default
            1 -> Explicit
            2 -> Safe
            3 -> AgeRestricted
            else -> Unknown(value)
        }
    }
}
