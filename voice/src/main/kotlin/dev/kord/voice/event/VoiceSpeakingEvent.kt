package dev.kord.voice.event

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class VoiceSpeakingEvent(
    @SerialName("user_id")
    val userId: Snowflake,
    val ssrc: Int,
    val speaking: Boolean
) : VoiceEvent() {
}