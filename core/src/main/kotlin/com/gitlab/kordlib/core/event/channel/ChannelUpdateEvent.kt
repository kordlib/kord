package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelUpdateEvent : Event {
    val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

class CategoryUpdateEvent (override val channel: Category, override val shard: Int) : ChannelUpdateEvent
class DMChannelUpdateEvent (override val channel: DmChannel, override val shard: Int) : ChannelUpdateEvent
class NewsChannelUpdateEvent (override val channel: NewsChannel, override val shard: Int) : ChannelUpdateEvent
class StoreChannelUpdateEvent (override val channel: StoreChannel, override val shard: Int) : ChannelUpdateEvent
class TextChannelUpdateEvent (override val channel: TextChannel, override val shard: Int) : ChannelUpdateEvent
class VoiceChannelUpdateEvent (override val channel: VoiceChannel, override val shard: Int) : ChannelUpdateEvent
