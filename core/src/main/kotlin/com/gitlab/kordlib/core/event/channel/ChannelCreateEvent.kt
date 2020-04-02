package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelCreateEvent : Event {
    val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

class CategoryCreateEvent (override val channel: Category, override val shard: Int) : ChannelCreateEvent
class DMChannelCreateEvent (override val channel: DmChannel, override val shard: Int) : ChannelCreateEvent
class NewsChannelCreateEvent (override val channel: NewsChannel, override val shard: Int) : ChannelCreateEvent
class StoreChannelCreateEvent (override val channel: StoreChannel, override val shard: Int) : ChannelCreateEvent
class TextChannelCreateEvent (override val channel: TextChannel, override val shard: Int) : ChannelCreateEvent
class VoiceChannelCreateEvent (override val channel: VoiceChannel, override val shard: Int) : ChannelCreateEvent
