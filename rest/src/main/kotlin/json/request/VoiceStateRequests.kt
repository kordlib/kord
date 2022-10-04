package dev.kord.rest.json.request

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CurrentVoiceStateModifyRequest(
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    val suppress: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("request_to_speak_timestamp")
    val requestToSpeakTimestamp: Optional<Instant?> = Optional.Missing(),
)

@Serializable
public data class VoiceStateModifyRequest(
    @SerialName("channel_id")
    val channelId: Snowflake,
    val suppress: OptionalBoolean = OptionalBoolean.Missing
)
