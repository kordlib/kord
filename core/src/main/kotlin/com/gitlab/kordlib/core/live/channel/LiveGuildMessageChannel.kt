package com.gitlab.kordlib.core.live.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.channel.*
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent

@KordPreview
fun GuildMessageChannel.live() = LiveGuildMessageChannel(this)

@KordPreview
class LiveGuildMessageChannel(channel: GuildMessageChannel) : LiveChannel(), Entity by channel {

    override var channel: GuildMessageChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is ChannelCreateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelUpdateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()

        else -> Unit
    }

}