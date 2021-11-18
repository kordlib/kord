package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordGuildScheduledEvent
import dev.kord.common.entity.GuildScheduledEventEntityMetadata
import dev.kord.common.entity.GuildScheduledEventStatus
import dev.kord.common.entity.ScheduledEntityType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.map
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class GuildScheduledEventData(
    val id: Snowflake,
    val guildId: Snowflake,
    val channelId: Snowflake?,
    val creatorId: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: String,
    val description: Optional<String> = Optional.Missing(),
    val scheduledStartTime: Instant,
    val scheduledEndTime: Instant?,
    val privacyLevel: StageInstancePrivacyLevel,
    val status: GuildScheduledEventStatus,
    val entityId: Snowflake?,
    val entityType: ScheduledEntityType,
    val entityMetadata: GuildScheduledEventEntityMetadata,
    val creator: Optional<UserData> = Optional.Missing(),
    val userCount: Int
) {
    public companion object {
        public fun from(event: DiscordGuildScheduledEvent): GuildScheduledEventData = GuildScheduledEventData(
            event.id,
            event.guildId,
            event.channelId,
            event.creatorId,
            event.name,
            event.description,
            event.scheduledStartTime,
            event.scheduledEndTime,
            event.privacyLevel,
            event.status,
            event.entityId,
            event.entityType,
            event.entityMetadata,
            event.creator.map { UserData.from(it) },
            event.userCount
        )
    }
}

