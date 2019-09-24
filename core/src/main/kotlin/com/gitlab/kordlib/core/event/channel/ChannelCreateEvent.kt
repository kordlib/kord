package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelCreateEvent : Event {
    val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

class CategoryCreateEvent internal constructor(override val channel: Category) : ChannelCreateEvent
class DMChannelCreateEvent internal constructor(override val channel: DmChannel) : ChannelCreateEvent
class NewsChannelCreateEvent internal constructor(override val channel: NewsChannel) : ChannelCreateEvent
class StoreChannelCreateEvent internal constructor(override val channel: StoreChannel) : ChannelCreateEvent
class TextChannelCreateEvent internal constructor(override val channel: TextChannel) : ChannelCreateEvent
class VoiceChannelCreateEvent internal constructor(override val channel: VoiceChannel) : ChannelCreateEvent
