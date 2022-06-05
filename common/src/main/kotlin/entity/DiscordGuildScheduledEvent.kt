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
 * Representation of a
 * [Guild Scheduled Event Structure](https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-guild-scheduled-event-structure).
 *
 * @property id The id of the scheduled event.
 * @property guildId The guild id which the scheduled event belongs to.
 * @property channelId The channel id in which the scheduled event will be hosted, or `null` if [entityType] is
 * [External][ScheduledEntityType.External].
 * @property creatorId The id of the user that created the scheduled event.
 * @property name The name of the scheduled event.
 * @property description The description of the scheduled event.
 * @property scheduledStartTime The [Instant] in which the scheduled event will start.
 * @property scheduledEndTime The [Instant] in which the scheduled event will end, if any.
 * @property privacyLevel The [privacy level][GuildScheduledEventPrivacyLevel] of the scheduled event.
 * @property status The [status][GuildScheduledEventStatus] of the scheduled event.
 * @property entityType The [type][ScheduledEntityType] of the scheduled event.
 * @property entityId The id of an entity associated with a guild scheduled event.
 * @property entityMetadata Additional [metadata][GuildScheduledEventEntityMetadata] for the guild scheduled event.
 * @property creator The [user][DiscordUser] that created the scheduled event.
 * @property userCount The number of users subscribed to the scheduled event.
 * @property image The [cover image hash](https://discord.com/developers/docs/reference#image-formatting) of the
 * scheduled event.
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
    val description: Optional<String?> = Optional.Missing(),
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
    val image: Optional<String?> = Optional.Missing(),
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
