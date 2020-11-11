package com.gitlab.kordlib.core.event.channel.data

import com.gitlab.kordlib.common.entity.DiscordGuildMember
import com.gitlab.kordlib.common.entity.DiscordTyping
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TypingStartEventData(
        val channelId: Snowflake,
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        val userId: Snowflake,
        val timestamp: Long,
        val member: Optional<DiscordGuildMember> = Optional.Missing()
) {
    companion object {
        fun from(entity: DiscordTyping): TypingStartEventData = with(entity){
            TypingStartEventData(channelId, guildId, userId, timestamp, member)
        }
    }
}