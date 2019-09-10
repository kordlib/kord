package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.event.Event

class MessageUpdateEvent internal constructor(
        private val messageId: Snowflake,
        private val channelId: Snowflake,
        val old: Message?,
        override val kord: Kord
) : Event {

    suspend fun getMessage(): Message = kord.getMessage(channelId, messageId)!!

}