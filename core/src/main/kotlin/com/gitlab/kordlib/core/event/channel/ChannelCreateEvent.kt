package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelCreateEvent : Event {
    val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

class CategoryCreateEvent(override val channel: Category) : ChannelCreateEvent
class DMChannelCreateEvent(override val channel: DmChannel) : ChannelCreateEvent
class NewsChannelCreateEvent(override val channel: NewsChannel) : ChannelCreateEvent
class StoreChannelCreateEvent(override val channel: StoreChannel) : ChannelCreateEvent
class TextChannelCreateEvent(override val channel: TextChannel) : ChannelCreateEvent
class VoiceChannelCreateEvent(override val channel: VoiceChannel) : ChannelCreateEvent
