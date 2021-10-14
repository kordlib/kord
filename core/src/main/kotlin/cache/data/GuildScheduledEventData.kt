package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordGuildScheduledEvent
import dev.kord.common.entity.GuildScheduledEventEntityMetadata
import dev.kord.common.entity.GuildScheduledEventStatus
import dev.kord.common.entity.ScheduledEntityType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.optional.Optional
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
public data class GuildScheduledEventData(
    val id: Snowflake,
    val guildId: Snowflake,
    val channelId: Snowflake?,
    val name: String,
    val description: Optional<String> = Optional.Missing(),
    val image: String?,
    val scheduledStartTime: Instant,
    val scheduledEndTime: Instant?,
    val privacyLevel: StageInstancePrivacyLevel,
    val status: GuildScheduledEventStatus,
    val type: ScheduledEntityType,
    val entityId: Snowflake?,
    val entityType: ScheduledEntityType,
    val entityMetadata: GuildScheduledEventEntityMetadata,
    val skuIds: List<Snowflake>,
    val skus: JsonArray,
    val userCount: Int
) {
    public companion object {
        public fun from(event: DiscordGuildScheduledEvent): GuildScheduledEventData = GuildScheduledEventData(
            event.id,
            event.guildId,
            event.channelId,
            event.name,
            event.description,
            event.image,
            event.scheduledStartTime,
            event.scheduledEndTime,
            event.privacyLevel,
            event.status,
            event.type,
            event.entityId,
            event.entityType,
            event.entityMetadata,
            event.skuIds,
            event.skus,
            event.userCount
        )
    }
}

