package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelCreateEvent : Event {
    val channel: Channel
}

 class CategoryCreateEvent internal constructor(override val channel: Category, override val kord: Kord) : ChannelCreateEvent
 class DMChannelCreateEvent internal constructor(override val channel: DmChannel, override val kord: Kord): ChannelCreateEvent
 class NewsChannelCreateEvent internal constructor(override val channel: NewsChannel, override val kord: Kord): ChannelCreateEvent
 class StoreChannelCreateEvent internal constructor(override val channel: StoreChannel, override val kord: Kord): ChannelCreateEvent
 class TextChannelCreateEvent internal constructor(override val channel: TextChannel, override val kord: Kord): ChannelCreateEvent
 class VoiceChannelCreateEvent internal constructor(override val channel: VoiceChannel, override val kord: Kord): ChannelCreateEvent
