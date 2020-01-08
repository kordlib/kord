package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.common.entity.DiscordPartialMessage
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.event.Event

class MessageUpdateEvent internal constructor(
        val messageId: Snowflake,
        val channelId: Snowflake,
        val new: DiscordPartialMessage,
        val old: Message?,
        override val kord: Kord
) : Event {

    suspend fun getMessage(): Message = kord.getMessage(channelId, messageId)!!

}