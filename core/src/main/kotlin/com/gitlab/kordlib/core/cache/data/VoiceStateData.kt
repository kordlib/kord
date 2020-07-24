package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.DiscordVoiceState
import kotlinx.serialization.Serializable

val VoiceStateData.id get() = "$userId$guildId"

@Serializable
data class VoiceStateData(
        val guildId: Long?,
        val channelId: Long?,
        val userId: Long,
        val sessionId: String,
        val mute: Boolean,
        val deaf: Boolean,
        val selfMute: Boolean,
        val selfDeaf: Boolean,
        val selfStream: Boolean,
        val suppress: Boolean
) {

    companion object {
        val description = description(VoiceStateData::id)

        fun from(entity: DiscordVoiceState) = with(entity) {
            VoiceStateData(
                    guildId?.toLong(),
                    channelId?.toLong(),
                    userId.toLong(),
                    sessionId,
                    mute,
                    deaf,
                    selfMute,
                    selfDeaf,
                    selfStream ?: false,
                    suppress
            )
        }
    }


}