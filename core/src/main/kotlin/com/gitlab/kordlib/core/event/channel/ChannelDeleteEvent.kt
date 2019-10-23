package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelDeleteEvent : Event {
    val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

class CategoryDeleteEvent internal constructor(override val channel: Category) : ChannelDeleteEvent
class DMChannelDeleteEvent internal constructor(override val channel: DmChannel) : ChannelDeleteEvent
class NewsChannelDeleteEvent internal constructor(override val channel: NewsChannel) : ChannelDeleteEvent
class StoreChannelDeleteEvent internal constructor(override val channel: StoreChannel) : ChannelDeleteEvent
class TextChannelDeleteEvent internal constructor(override val channel: TextChannel) : ChannelDeleteEvent
class VoiceChannelDeleteEvent internal constructor(override val channel: VoiceChannel) : ChannelDeleteEvent
