@file:GenerateKordEnum(
    name = "GuildScheduledEventPrivacyLevel", valueType = INT,
    entries = [Entry("GuildOnly", intValue = 2, kDoc = "The scheduled event is only accessible to guild members.")],
)

@file:GenerateKordEnum(
    name = "ScheduledEntityType", valueType = INT,
    entries = [
        Entry("StageInstance", intValue = 1),
        Entry("Voice", intValue = 2),
        Entry("External", intValue = 3),
    ],
)

@file:GenerateKordEnum(
    name = "GuildScheduledEventStatus", valueType = INT,
    entries = [
        Entry("Scheduled", intValue = 1),
        Entry("Active", intValue = 2),
        Entry("Completed", intValue = 3),
        Entry("Cancelled", intValue = 4),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

/**
 * Entity metadata for [DiscordGuildScheduledEvent].
 *
 * @property location location of the event
 */
@Serializable
public data class GuildScheduledEventEntityMetadata(
    val location: Optional<String> = Optional.Missing()
)
