package com.gitlab.kordlib.core.live.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.channel.DmChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.channel.DMChannelCreateEvent
import com.gitlab.kordlib.core.event.channel.DMChannelDeleteEvent
import com.gitlab.kordlib.core.event.channel.DMChannelUpdateEvent
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent

fun DmChannel.live() = LiveDmChannel(this)

@KordPreview
class LiveDmChannel(channel: DmChannel) : LiveChannel(), Entity by channel {

    override var channel: DmChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is DMChannelCreateEvent -> channel = event.channel
        is DMChannelUpdateEvent -> channel = event.channel
        is DMChannelDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()

        else -> Unit
    }

}