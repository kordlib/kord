package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelUpdateEvent : Event {
    val channel: Channel
}

 class CategoryUpdateEvent internal constructor(override val channel: Category, override val kord: Kord) : ChannelUpdateEvent
 class DMChannelUpdateEvent internal constructor(override val channel: DmChannel, override val kord: Kord): ChannelUpdateEvent
 class NewsChannelUpdateEvent internal constructor(override val channel: NewsChannel, override val kord: Kord): ChannelUpdateEvent
 class StoreChannelUpdateEvent internal constructor(override val channel: StoreChannel, override val kord: Kord): ChannelUpdateEvent
 class TextChannelUpdateEvent internal constructor(override val channel: TextChannel, override val kord: Kord): ChannelUpdateEvent
 class VoiceChannelUpdateEvent internal constructor(override val channel: VoiceChannel, override val kord: Kord): ChannelUpdateEvent
