package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildMessageChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.cache.data.ReactionRemoveEmojiData
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.ReactionEmoji
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.event.Event

class ReactionRemoveEmojiEvent(
        val data: ReactionRemoveEmojiData,
        override val kord: Kord
) : Event {

    /**
     * The id of the [GuildMessageChannel].
     */
    val channelId: Snowflake get() = Snowflake(data.channelId)

    val channel: MessageChannelBehavior get() = GuildMessageChannelBehavior(guildId = guildId, id = channelId, kord = kord)

    /**
     * The id of the [Guild].
     */
    val guildId: Snowflake get() = Snowflake(data.guildId)

    val guild: GuildBehavior get() = GuildBehavior(id = guildId, kord = kord)

    /**
     * The id of the message.
     */
    val messageId: Snowflake get() = Snowflake(data.messageId)

    val message: MessageBehavior get() = MessageBehavior(channelId = channelId, messageId = messageId, kord = kord)

    /**
     * The emoji that was removed.
     */
    val emoji: ReactionEmoji get() = ReactionEmoji.from(data.emoji)

    suspend fun getChannel(): GuildMessageChannel = kord.getChannel(channelId) as GuildMessageChannel

    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!

    suspend fun getMessage(): Message = kord.getMessage(channelId, messageId)!!


}