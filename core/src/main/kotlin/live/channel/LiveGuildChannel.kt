package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.Entity
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent

@KordPreview
fun GuildChannel.live() = LiveGuildChannel(this)

@KordPreview
class LiveGuildChannel(channel: GuildChannel) : LiveChannel(), Entity by channel {

    override var channel: GuildChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is ChannelCreateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelUpdateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()

        else -> Unit
    }

}