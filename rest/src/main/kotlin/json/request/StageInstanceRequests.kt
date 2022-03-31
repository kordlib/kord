package dev.kord.rest.json.request

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
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
)

@Serializable
public data class StageInstanceUpdateRequest(val topic: String)
