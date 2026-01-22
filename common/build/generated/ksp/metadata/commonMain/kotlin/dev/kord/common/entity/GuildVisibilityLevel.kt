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
 * See [GuildVisibilityLevel]s in the [Discord Developer Documentation](https://docs.discord.food/resources/discovery#guild-visibility).
 */
@Serializable(with = GuildVisibilityLevel.Serializer::class)
@KordPreview
public sealed class GuildVisibilityLevel(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other || (other is GuildVisibilityLevel && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "GuildVisibilityLevel.Unknown(value=$value)" else "GuildVisibilityLevel.${this::class.simpleName}"

    /**
     * An unknown [GuildVisibilityLevel].
     *
     * This is used as a fallback for [GuildVisibilityLevel]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : GuildVisibilityLevel(value)

    public object Public : GuildVisibilityLevel(1)

    public object Restricted : GuildVisibilityLevel(2)

    public object PublicWithRecruitment : GuildVisibilityLevel(3)

    internal object Serializer : KSerializer<GuildVisibilityLevel> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.GuildVisibilityLevel", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: GuildVisibilityLevel) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): GuildVisibilityLevel = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [GuildVisibilityLevel]s.
         */
        public val entries: List<GuildVisibilityLevel> by lazy(mode = PUBLICATION) {
            listOf(
                Public,
                Restricted,
                PublicWithRecruitment,
            )
        }

        /**
         * Returns an instance of [GuildVisibilityLevel] with [GuildVisibilityLevel.value] equal to the specified [value].
         */
        public fun from(`value`: Int): GuildVisibilityLevel = when (value) {
            1 -> Public
            2 -> Restricted
            3 -> PublicWithRecruitment
            else -> Unknown(value)
        }
    }
}
