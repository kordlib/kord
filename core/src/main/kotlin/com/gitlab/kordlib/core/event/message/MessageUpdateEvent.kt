package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.common.entity.DiscordPartialMessage
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.getChannel

class MessageUpdateEvent internal constructor(
        val messageId: Snowflake,
        val channelId: Snowflake,
        val new: DiscordPartialMessage,
        val old: Message?,
        override val kord: Kord
) : Event {

    /**
     * The behavior of the message that was updated.
     */
    val message: MessageBehavior get() = MessageBehavior(messageId = messageId, channelId = channelId, kord = kord)

    /**
     * The behavior of the channel in which the message was updated.
     */
    val channel: MessageChannelBehavior get() = MessageChannelBehavior(id = channelId, kord = kord)

    /**
     * Requests to get the message that was updated.
     */
    suspend fun getMessage(): Message = kord.getMessage(channelId, messageId)!!

    /**
     * Requests to get the message's channel.
     */
    suspend fun getChannel(): MessageChannel = kord.getChannel<MessageChannel>(channelId)!!
}