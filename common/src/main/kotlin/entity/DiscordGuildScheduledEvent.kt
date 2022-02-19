package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Representation of a [Guild Scheduled Event Structure](ADD LINK).
 *
 * @property id the id of the event
 * @property guildId the id of the guild the event is on
 * @property channelId the id of the channel the event is in
 * @property creatorId the id of the user that created the scheduled event
 * @property name the name of the event
 * @property description the description of the event
 * @property scheduledStartTime the [Instant] in which the event will start
 * @property scheduledEndTime the [Instant] in which the event wil stop, if any
 * @property privacyLevel the [event privacy level][GuildScheduledEventPrivacyLevel]
 * @property status the [event status][GuildScheduledEventStatus]
 * @property entityType the [ScheduledEntityType] of the event
 * @property entityId entity id
 * @property entityMetadata [metadata][GuildScheduledEventEntityMetadata] for the event
 * @property creator the [user][DiscordUser] that created the scheduled event
 * @property userCount users subscribed to the event
 */
@Serializable
public data class DiscordGuildScheduledEvent(
    val id: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("channel_id")
    val channelId: Snowflake?,
    @SerialName("creator_id")
    val creatorId: OptionalSnowflake? = OptionalSnowflake.Missing,
    val name: String,
    val description: Optional<String> = Optional.Missing(),
    @SerialName("scheduled_start_time")
    val scheduledStartTime: Instant,
    @SerialName("scheduled_end_time")
    val scheduledEndTime: Instant?,
    @SerialName("privacy_level")
    val privacyLevel: GuildScheduledEventPrivacyLevel,
    val status: GuildScheduledEventStatus,
    @SerialName("entity_type")
    val entityType: ScheduledEntityType,
    @SerialName("entity_id")
    val entityId: Snowflake?,
    @SerialName("entity_metadata")
    val entityMetadata: GuildScheduledEventEntityMetadata?,
    val creator: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("user_count")
    val userCount: OptionalInt = OptionalInt.Missing,
)

/** Privacy level of a [DiscordGuildScheduledEvent]. */
@Serializable(with = GuildScheduledEventPrivacyLevel.Serializer::class)
public sealed class GuildScheduledEventPrivacyLevel(public val value: Int) {

    /** The scheduled event is only accessible to guild members. */
    public object GuildOnly : GuildScheduledEventPrivacyLevel(2)

    /** An unknown privacy level. */
    public class Unknown(value: Int) : GuildScheduledEventPrivacyLevel(value)

    internal object Serializer : KSerializer<GuildScheduledEventPrivacyLevel> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("GuildScheduledEventPrivacyLevel", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): GuildScheduledEventPrivacyLevel {
            return when (val value = decoder.decodeInt()) {
                2 -> GuildOnly
                else -> Unknown(value)
            }
        }

        override fun serialize(encoder: Encoder, value: GuildScheduledEventPrivacyLevel) {
            encoder.encodeInt(value.value)
        }
    }
}

@Serializable(with = ScheduledEntityType.Serializer::class)
public sealed class ScheduledEntityType(public val value: Int) {
    public object StageInstance : ScheduledEntityType(1)
    public object Voice : ScheduledEntityType(2)
    public object External : ScheduledEntityType(3)
    public class Unknown(value: Int) : ScheduledEntityType(value)

    public companion object Serializer : KSerializer<ScheduledEntityType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ScheduledEntityType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ScheduledEntityType {
            return when (val value = decoder.decodeInt()) {
                1 -> StageInstance
                2 -> Voice
                3 -> External
                else -> Unknown(value)
            }
        }

        override fun serialize(encoder: Encoder, value: ScheduledEntityType): Unit = encoder.encodeInt(value.value)

    }
}

@Serializable(with = GuildScheduledEventStatus.Serializer::class)
public sealed class GuildScheduledEventStatus(public val value: Int) {
    public object Scheduled : GuildScheduledEventStatus(1)
    public object Active : GuildScheduledEventStatus(2)
    public object Completed : GuildScheduledEventStatus(3)
    public object Cancelled : GuildScheduledEventStatus(4)
    public class Unknown(value: Int) : GuildScheduledEventStatus(value)

    public companion object Serializer : KSerializer<GuildScheduledEventStatus> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GuildScheduledEventStatus", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): GuildScheduledEventStatus {
            return when (val value = decoder.decodeInt()) {
                1 -> Scheduled
                2 -> Active
                3 -> Completed
                4 -> Cancelled
                else -> Unknown(value)
            }
        }

        override fun serialize(encoder: Encoder, value: GuildScheduledEventStatus): Unit = encoder.encodeInt(value.value)

    }
}

/**
 * Entity metadata for [DiscordGuildScheduledEvent].
 *
 * @property location location of the event
 */
@Serializable
public data class GuildScheduledEventEntityMetadata(
    val location: Optional<String> = Optional.Missing()
)
