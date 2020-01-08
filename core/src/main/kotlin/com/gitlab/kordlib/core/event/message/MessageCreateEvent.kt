package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.event.Event

class MessageCreateEvent(val message: Message) : Event {
    override val kord: Kord get() = message.kord
}
