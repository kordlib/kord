package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelDeleteEvent : Event {
    val channel: Channel
}

 class CategoryDeleteEvent internal constructor(override val channel: Category, override val kord: Kord) : ChannelDeleteEvent
 class DMChannelDeleteEvent internal constructor(override val channel: DmChannel, override val kord: Kord): ChannelDeleteEvent
 class NewsChannelDeleteEvent internal constructor(override val channel: NewsChannel, override val kord: Kord): ChannelDeleteEvent
 class StoreChannelDeleteEvent internal constructor(override val channel: StoreChannel, override val kord: Kord): ChannelDeleteEvent
 class TextChannelDeleteEvent internal constructor(override val channel: TextChannel, override val kord: Kord): ChannelDeleteEvent
 class VoiceChannelDeleteEvent internal constructor(override val channel: VoiceChannel, override val kord: Kord): ChannelDeleteEvent
