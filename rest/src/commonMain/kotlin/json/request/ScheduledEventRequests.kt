package dev.kord.rest.json.request

import dev.kord.common.entity.GuildScheduledEventEntityMetadata
import dev.kord.common.entity.GuildScheduledEventPrivacyLevel
import dev.kord.common.entity.GuildScheduledEventStatus
import dev.kord.common.entity.ScheduledEntityType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class GuildScheduledEventCreateRequest(
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("entity_metadata")
    val entityMetadata: Optional<GuildScheduledEventEntityMetadata> = Optional.Missing(),
    val name: String,
    @SerialName("privacy_level")
    val privacyLevel: GuildScheduledEventPrivacyLevel,
    @SerialName("scheduled_start_time")
    val scheduledStartTime: Instant,
    @SerialName("scheduled_end_time")
    val scheduledEndTime: Optional<Instant> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    @SerialName("entity_type")
    val entityType: ScheduledEntityType,
    val image: Optional<String> = Optional.Missing(),
)

@Serializable
public data class ScheduledEventModifyRequest(
    @SerialName("channel_id")
    val channelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("entity_metadata")
    val entityMetadata: Optional<GuildScheduledEventEntityMetadata?> = Optional.Missing(),
    val name: Optional<String> = Optional.Missing(),
    @SerialName("privacy_level")
    val privacyLevel: Optional<GuildScheduledEventPrivacyLevel> = Optional.Missing(),
    @SerialName("scheduled_start_time")
    val scheduledStartTime: Optional<Instant> = Optional.Missing(),
    @SerialName("scheduled_end_time")
    val scheduledEndTime: Optional<Instant> = Optional.Missing(),
    val description: Optional<String?> = Optional.Missing(),
    @SerialName("entity_type")
    val entityType: Optional<ScheduledEntityType> = Optional.Missing(),
    val status: Optional<GuildScheduledEventStatus> = Optional.Missing(),
    val image: Optional<String> = Optional.Missing(),
)
