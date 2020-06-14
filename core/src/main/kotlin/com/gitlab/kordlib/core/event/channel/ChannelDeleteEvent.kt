package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelDeleteEvent : Event {
    val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

class CategoryDeleteEvent(override val channel: Category) : ChannelDeleteEvent
class DMChannelDeleteEvent(override val channel: DmChannel) : ChannelDeleteEvent
class NewsChannelDeleteEvent(override val channel: NewsChannel) : ChannelDeleteEvent
class StoreChannelDeleteEvent(override val channel: StoreChannel) : ChannelDeleteEvent
class TextChannelDeleteEvent(override val channel: TextChannel) : ChannelDeleteEvent
class VoiceChannelDeleteEvent(override val channel: VoiceChannel) : ChannelDeleteEvent
