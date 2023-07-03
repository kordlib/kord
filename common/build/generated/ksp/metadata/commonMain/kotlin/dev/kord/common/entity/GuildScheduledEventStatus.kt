// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection",
                "RedundantUnitReturnType"))

package dev.kord.common.entity

import dev.kord.common.`annotation`.KordUnsafe
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
@OptIn(KordUnsafe::class)
public sealed class GuildScheduledEventStatus(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is GuildScheduledEventStatus && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    public final override fun toString(): String =
            "GuildScheduledEventStatus.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [GuildScheduledEventStatus].
     *
     * This is used as a fallback for [GuildScheduledEventStatus]s that haven't been added to Kord
     * yet.
     */
    public class Unknown @KordUnsafe constructor(
        `value`: Int,
    ) : GuildScheduledEventStatus(value)

    public object Scheduled : GuildScheduledEventStatus(1)

    public object Active : GuildScheduledEventStatus(2)

    public object Completed : GuildScheduledEventStatus(3)

    public object Cancelled : GuildScheduledEventStatus(4)

    internal object Serializer : KSerializer<GuildScheduledEventStatus> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.GuildScheduledEventStatus",
                PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder, `value`: GuildScheduledEventStatus) =
                encoder.encodeInt(value.value)

        public override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> Scheduled
            2 -> Active
            3 -> Completed
            4 -> Cancelled
            else -> Unknown(value)
        }
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

    }
}
