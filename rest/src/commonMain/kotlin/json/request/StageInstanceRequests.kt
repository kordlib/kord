package dev.kord.rest.json.request

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class StageInstanceCreateRequest(
    @SerialName("channel_id")
    val channelId: Snowflake,
    val topic: String,
    @SerialName("privacy_level")
    val privacyLevel: Optional<StageInstancePrivacyLevel> = Optional.Missing(),
    @SerialName("send_start_notification")
    val sendStartNotification: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("guild_scheduled_event_id")
    val guildScheduledEventId: OptionalSnowflake = OptionalSnowflake.Missing,
)

@Serializable
public data class StageInstanceModifyRequest(
    val topic: Optional<String> = Optional.Missing(),
    @SerialName("privacy_level")
    val privacyLevel: Optional<StageInstancePrivacyLevel> = Optional.Missing(),
)
