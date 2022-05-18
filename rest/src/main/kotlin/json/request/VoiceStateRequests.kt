package dev.kord.rest.json.request

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.map
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CurrentVoiceStateModifyRequest(
    @SerialName("channel_id")
    val channelId: Snowflake,
    val suppress: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("request_to_speak_timestamp")
    val requestToSpeakTimestamp: Optional<Instant?> = Optional.Missing(),
) {
    @Deprecated(
        "requestToSpeakTimeStamp was renamed to requestToSpeakTimestamp.",
        ReplaceWith("requestToSpeakTimestamp"),
        DeprecationLevel.ERROR,
    )
    val requestToSpeakTimeStamp: Optional<String>
        get() = requestToSpeakTimestamp.map { it.toString() }.coerceToMissing()
}


@Serializable
public data class VoiceStateModifyRequest(
    @SerialName("channel_id")
    val channelId: Snowflake,
    val suppress: OptionalBoolean = OptionalBoolean.Missing
)
