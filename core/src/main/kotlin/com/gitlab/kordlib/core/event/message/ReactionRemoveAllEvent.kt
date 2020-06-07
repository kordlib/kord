package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.event.Event

class ReactionRemoveAllEvent(
        val channelId: Snowflake,
        val messageId: Snowflake,
        val guildId: Snowflake?,
        override val kord: Kord
) : Event {

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    val message: MessageBehavior get() = MessageBehavior(channelId, messageId, kord)

    suspend fun getChannel(): MessageChannel = kord.getChannel(channelId) as MessageChannel

    suspend fun getGuild(): Guild? = guildId?.let { kord.getGuild(it) }

    suspend fun getMessage(): Message = kord.getMessage(channelId, messageId)!!

}