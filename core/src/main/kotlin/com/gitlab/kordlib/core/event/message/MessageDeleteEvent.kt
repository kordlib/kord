package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.event.Event

class MessageDeleteEvent(
        val messageId: Snowflake,
        val channelId: Snowflake,
        val guildId: Snowflake?,
        val message: Message?,
        override val kord: Kord
): Event {

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    suspend fun getChannel(): MessageChannel = kord.getChannel(channelId) as MessageChannel

    suspend fun getGuild(): Guild? = guildId?.let { kord.getGuild(it) }

}
