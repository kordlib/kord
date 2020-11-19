package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.DiscordGuildMember
import com.gitlab.kordlib.common.entity.DiscordVoiceState
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import com.gitlab.kordlib.common.entity.optional.mapSnowflake
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
                    member.mapSnowflake { it.user.value!!.id },
                    sessionId,
                    mute,
                    deaf,
                    selfMute,
                    selfDeaf,
                    selfStream,
                    suppress
            )
        }
    }


}