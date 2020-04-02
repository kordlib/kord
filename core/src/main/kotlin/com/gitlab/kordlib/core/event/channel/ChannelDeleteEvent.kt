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

class CategoryDeleteEvent (override val channel: Category, override val shard: Int) : ChannelDeleteEvent
class DMChannelDeleteEvent (override val channel: DmChannel, override val shard: Int) : ChannelDeleteEvent
class NewsChannelDeleteEvent (override val channel: NewsChannel, override val shard: Int) : ChannelDeleteEvent
class StoreChannelDeleteEvent (override val channel: StoreChannel, override val shard: Int) : ChannelDeleteEvent
class TextChannelDeleteEvent (override val channel: TextChannel, override val shard: Int) : ChannelDeleteEvent
class VoiceChannelDeleteEvent (override val channel: VoiceChannel, override val shard: Int) : ChannelDeleteEvent
