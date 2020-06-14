package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelUpdateEvent : Event {
    val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

class CategoryUpdateEvent(override val channel: Category) : ChannelUpdateEvent
class DMChannelUpdateEvent(override val channel: DmChannel) : ChannelUpdateEvent
class NewsChannelUpdateEvent(override val channel: NewsChannel) : ChannelUpdateEvent
class StoreChannelUpdateEvent(override val channel: StoreChannel) : ChannelUpdateEvent
class TextChannelUpdateEvent(override val channel: TextChannel) : ChannelUpdateEvent
class VoiceChannelUpdateEvent(override val channel: VoiceChannel) : ChannelUpdateEvent
