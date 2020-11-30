package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.Entity
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.*
import dev.kord.core.event.guild.GuildDeleteEvent

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