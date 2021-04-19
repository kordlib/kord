package dev.kord.core.cache.data

import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordGuildMember
import dev.kord.common.entity.DiscordVoiceState
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.mapSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val VoiceStateData.id get() = "$userId$guildId"

@Serializable
data class VoiceStateData(
    /*
     We assume we're only getting voice state updates from guilds.
     If Discord allows people to voice chat with bots we're in trouble.
     (And not just because this code would break).
     */
    val guildId: Snowflake,
    val channelId: Snowflake? = null,
    val userId: Snowflake,
    val memberId: OptionalSnowflake = OptionalSnowflake.Missing,
    val sessionId: String,
    val deaf: Boolean,
    val mute: Boolean,
    val selfDeaf: Boolean,
    val selfMute: Boolean,
    val selfStream: OptionalBoolean = OptionalBoolean.Missing,
    val suppress: Boolean,
) {

    companion object {
        val description = description(VoiceStateData::id)

        fun from(guildId: Snowflake, entity: DiscordVoiceState) = with(entity) {
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
                suppress = suppress
            )
        }
    }


}