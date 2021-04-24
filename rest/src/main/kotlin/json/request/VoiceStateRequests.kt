package dev.kord.rest.json.request

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentVoiceStateModifyRequest(
    @SerialName("channel_id")
    val channelId: Snowflake,
    val suppress: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("request_to_speak_timestamp")
    val requestToSpeakTimeStamp: Optional<String> = Optional.Missing()
)


@Serializable
data class VoiceStateModifyRequest(
    @SerialName("channel_id")
    val channelId: Snowflake,
    val suppress: OptionalBoolean = OptionalBoolean.Missing
)