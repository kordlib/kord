package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.builder.message.NewMessageBuilder
import com.gitlab.kordlib.core.entity.Entity

interface ChannelBehavior : Entity {

    suspend fun createMessage(builder: NewMessageBuilder): Nothing = TODO()
    suspend fun createMessage(content: String): Nothing = TODO()

}

suspend inline fun ChannelBehavior.createMessage(block: NewMessageBuilder.() -> Unit): Nothing = createMessage(NewMessageBuilder().apply(block))
