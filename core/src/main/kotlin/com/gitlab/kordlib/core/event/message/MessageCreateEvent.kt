package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.ban
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.DmChannel
import com.gitlab.kordlib.core.event.Event

class MessageCreateEvent(val message: Message) : Event {
    override val kord: Kord get() = message.kord
}
