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
 * See [GuildScheduledEventPrivacyLevel]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-guild-scheduled-event-privacy-level).
 */
@Serializable(with = GuildScheduledEventPrivacyLevel.Serializer::class)
public sealed class GuildScheduledEventPrivacyLevel(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is GuildScheduledEventPrivacyLevel && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "GuildScheduledEventPrivacyLevel.Unknown(value=$value)"
            else "GuildScheduledEventPrivacyLevel.${this::class.simpleName}"

    /**
     * An unknown [GuildScheduledEventPrivacyLevel].
     *
     * This is used as a fallback for [GuildScheduledEventPrivacyLevel]s that haven't been added to
     * Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
        @Suppress(names = arrayOf("UNUSED_PARAMETER"))
        unused: Nothing?,
    ) : GuildScheduledEventPrivacyLevel(value) {
        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Replaced by 'GuildScheduledEventPrivacyLevel.from()'.",
            replaceWith = ReplaceWith(expression = "GuildScheduledEventPrivacyLevel.from(value)",
                        imports =
                        arrayOf("dev.kord.common.entity.GuildScheduledEventPrivacyLevel")),
        )
        public constructor(`value`: Int) : this(value, null)
    }

    /**
     * The scheduled event is only accessible to guild members.
     */
    public object GuildOnly : GuildScheduledEventPrivacyLevel(2)

    internal object Serializer : KSerializer<GuildScheduledEventPrivacyLevel> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.GuildScheduledEventPrivacyLevel",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: GuildScheduledEventPrivacyLevel) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): GuildScheduledEventPrivacyLevel =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [GuildScheduledEventPrivacyLevel]s.
         */
        public val entries: List<GuildScheduledEventPrivacyLevel> by lazy(mode = PUBLICATION) {
            listOf(
                GuildOnly,
            )
        }


        /**
         * Returns an instance of [GuildScheduledEventPrivacyLevel] with
         * [GuildScheduledEventPrivacyLevel.value] equal to the specified [value].
         */
        public fun from(`value`: Int): GuildScheduledEventPrivacyLevel = when (value) {
            2 -> GuildOnly
            else -> Unknown(value, null)
        }
    }
}
