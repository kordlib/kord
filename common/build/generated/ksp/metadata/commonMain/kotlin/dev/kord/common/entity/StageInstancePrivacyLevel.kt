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
 * See [StageInstancePrivacyLevel]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/stage-instance#stage-instance-object-privacy-level).
 */
@Serializable(with = StageInstancePrivacyLevel.Serializer::class)
public sealed class StageInstancePrivacyLevel(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is StageInstancePrivacyLevel && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "StageInstancePrivacyLevel.Unknown(value=$value)"
            else "StageInstancePrivacyLevel.${this::class.simpleName}"

    /**
     * An unknown [StageInstancePrivacyLevel].
     *
     * This is used as a fallback for [StageInstancePrivacyLevel]s that haven't been added to Kord
     * yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : StageInstancePrivacyLevel(value)

    /**
     * The Stage instance is visible publicly.
     */
    @Deprecated(message = "Stages are no longer discoverable")
    public object Public : StageInstancePrivacyLevel(1)

    /**
     * The Stage instance is visible to only guild members.
     */
    public object GuildOnly : StageInstancePrivacyLevel(2)

    internal object Serializer : KSerializer<StageInstancePrivacyLevel> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.StageInstancePrivacyLevel",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: StageInstancePrivacyLevel) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): StageInstancePrivacyLevel =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [StageInstancePrivacyLevel]s.
         */
        public val entries: List<StageInstancePrivacyLevel> by lazy(mode = PUBLICATION) {
            listOf(
                @Suppress("DEPRECATION") Public,
                GuildOnly,
            )
        }

        /**
         * Returns an instance of [StageInstancePrivacyLevel] with [StageInstancePrivacyLevel.value]
         * equal to the specified [value].
         */
        public fun from(`value`: Int): StageInstancePrivacyLevel = when (value) {
            1 -> @Suppress("DEPRECATION") Public
            2 -> GuildOnly
            else -> Unknown(value)
        }
    }
}
