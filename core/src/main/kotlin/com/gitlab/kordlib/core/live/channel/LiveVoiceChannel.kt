package com.gitlab.kordlib.core.live.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.channel.VoiceChannelCreateEvent
import com.gitlab.kordlib.core.event.channel.VoiceChannelDeleteEvent
import com.gitlab.kordlib.core.event.channel.VoiceChannelUpdateEvent
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent

@KordPreview
fun VoiceChannel.live() = LiveVoiceChannel(this)

@KordPreview
class LiveVoiceChannel(channel: VoiceChannel) : LiveChannel(), Entity by channel {

    override var channel: VoiceChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is VoiceChannelCreateEvent -> channel = event.channel
        is VoiceChannelUpdateEvent -> channel = event.channel
        is VoiceChannelDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()

        else -> Unit
    }

}