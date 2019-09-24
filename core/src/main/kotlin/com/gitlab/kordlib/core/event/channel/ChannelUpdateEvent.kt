package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelUpdateEvent : Event {
    val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

class CategoryUpdateEvent internal constructor(override val channel: Category) : ChannelUpdateEvent
class DMChannelUpdateEvent internal constructor(override val channel: DmChannel) : ChannelUpdateEvent
class NewsChannelUpdateEvent internal constructor(override val channel: NewsChannel) : ChannelUpdateEvent
class StoreChannelUpdateEvent internal constructor(override val channel: StoreChannel) : ChannelUpdateEvent
class TextChannelUpdateEvent internal constructor(override val channel: TextChannel) : ChannelUpdateEvent
class VoiceChannelUpdateEvent internal constructor(override val channel: VoiceChannel) : ChannelUpdateEvent
