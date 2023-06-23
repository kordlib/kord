package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordVoiceState
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.mapSnowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

public val VoiceStateData.id: String
    get() = "$userId$guildId"

@Serializable
public data class VoiceStateData(
    /*
     We assume we're only getting voice state updates from guilds.
     If Discord allows people to voice chat with bots we're in trouble.
     (And not just because this code would break).
     */
    val guildId: Snowflake,
    val channelId: Snowflake?,
    val userId: Snowflake,
    val memberId: OptionalSnowflake = OptionalSnowflake.Missing,
    val sessionId: String,
    val deaf: Boolean,
    val mute: Boolean,
    val selfDeaf: Boolean,
    val selfMute: Boolean,
    val suppress: Boolean,
    val selfVideo: Boolean,
    val selfStream: OptionalBoolean = OptionalBoolean.Missing,
    val requestToSpeakTimestamp: Instant?,
) {
    public companion object {
        public val description: DataDescription<VoiceStateData, String> = description(VoiceStateData::id)

        public fun from(guildId: Snowflake, entity: DiscordVoiceState): VoiceStateData = with(entity) {
            VoiceStateData(
                guildId = guildId,
                channelId = channelId,
                userId = userId,
                memberId = member.mapSnowflake { it.user.value!!.id },
                sessionId = sessionId,
                deaf = deaf,
                mute = mute,
                selfDeaf = selfDeaf,
                selfMute = selfMute,
                selfStream = selfStream,
                selfVideo = selfVideo,
                suppress = suppress,
                requestToSpeakTimestamp = requestToSpeakTimestamp
            )
        }
    }
}
