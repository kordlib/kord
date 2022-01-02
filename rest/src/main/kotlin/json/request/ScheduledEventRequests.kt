package dev.kord.rest.json.request

import dev.kord.common.entity.GuildScheduledEventEntityMetadata
import dev.kord.common.entity.GuildScheduledEventStatus
import dev.kord.common.entity.ScheduledEntityType
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ScheduledEventModifyRequest(
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    val entityMetadata: Optional<GuildScheduledEventEntityMetadata> = Optional.Missing(),
    val name: Optional<String> = Optional.Missing(),
    @SerialName("privacy_level")
    val privacyLevel: Optional<StageInstancePrivacyLevel> = Optional.Missing(),
    @SerialName("scheduled_start_time")
    val scheduledStartTime: Optional<Instant> = Optional.Missing(),
    @SerialName("scheduled_end_time")
    val scheduledEndTime: Optional<Instant> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    @SerialName("entity_type")
    val entityType: Optional<ScheduledEntityType>,
    val status: Optional<GuildScheduledEventStatus> = Optional.Missing()
)
