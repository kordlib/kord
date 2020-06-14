package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.event.Event
import java.time.Instant

class TypingStartEvent(
        val channelId: Snowflake,
        val userId: Snowflake,
        val guildId: Snowflake?,
        val started: Instant,
        override val kord: Kord
) : Event {

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    val user: UserBehavior get() = UserBehavior(userId, kord)

    suspend fun getChannel(): MessageChannel = kord.getChannel(channelId) as MessageChannel

    suspend fun getUser(): User = kord.getUser(userId) as User

    suspend fun getGuild(): Guild? = guildId?.let { kord.getGuild(it) }
}