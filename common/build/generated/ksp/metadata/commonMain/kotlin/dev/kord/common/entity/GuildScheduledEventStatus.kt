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
 * See [GuildScheduledEventStatus]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-guild-scheduled-event-status).
 */
@Serializable(with = GuildScheduledEventStatus.Serializer::class)
public sealed class GuildScheduledEventStatus(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is GuildScheduledEventStatus && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "GuildScheduledEventStatus.Unknown(value=$value)"
            else "GuildScheduledEventStatus.${this::class.simpleName}"

    /**
     * An unknown [GuildScheduledEventStatus].
     *
     * This is used as a fallback for [GuildScheduledEventStatus]s that haven't been added to Kord
     * yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : GuildScheduledEventStatus(value)

    public object Scheduled : GuildScheduledEventStatus(1)

    public object Active : GuildScheduledEventStatus(2)

    public object Completed : GuildScheduledEventStatus(3)

    public object Cancelled : GuildScheduledEventStatus(4)

    internal object Serializer : KSerializer<GuildScheduledEventStatus> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.GuildScheduledEventStatus",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: GuildScheduledEventStatus) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): GuildScheduledEventStatus =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [GuildScheduledEventStatus]s.
         */
        public val entries: List<GuildScheduledEventStatus> by lazy(mode = PUBLICATION) {
            listOf(
                Scheduled,
                Active,
                Completed,
                Cancelled,
            )
        }


        /**
         * Returns an instance of [GuildScheduledEventStatus] with [GuildScheduledEventStatus.value]
         * equal to the specified [value].
         */
        public fun from(`value`: Int): GuildScheduledEventStatus = when (value) {
            1 -> Scheduled
            2 -> Active
            3 -> Completed
            4 -> Cancelled
            else -> Unknown(value)
        }
    }
}
