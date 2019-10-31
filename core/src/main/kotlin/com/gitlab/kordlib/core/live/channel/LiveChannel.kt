package com.gitlab.kordlib.core.live.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.VoiceStateUpdateEvent
import com.gitlab.kordlib.core.event.channel.ChannelCreateEvent
import com.gitlab.kordlib.core.event.channel.ChannelDeleteEvent
import com.gitlab.kordlib.core.event.channel.ChannelUpdateEvent
import com.gitlab.kordlib.core.event.guild.GuildCreateEvent
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent
import com.gitlab.kordlib.core.event.guild.GuildUpdateEvent
import com.gitlab.kordlib.core.event.message.*
import com.gitlab.kordlib.core.live.AbstractLiveEntity

fun  Channel.live() = when(this) {
    is DmChannel -> this.live()
    is NewsChannel -> this.live()
    is StoreChannel -> this.live()
    is TextChannel -> this.live()
    is VoiceChannel -> this.live()
    else -> error("unsupported channel type")
}

@KordPreview
abstract class LiveChannel : AbstractLiveEntity() {

    abstract val channel: Channel

    override fun filter(event: Event): Boolean = when(event) {
        is VoiceStateUpdateEvent -> event.state.channelId == channel.id

        is ReactionAddEvent -> event.channelId == channel.id
        is ReactionRemoveEvent -> event.channelId == channel.id
        is ReactionRemoveAllEvent -> event.channelId == channel.id

        is MessageCreateEvent -> event.message.channelId == channel.id
        is MessageUpdateEvent -> event.new.channelId == channel.id.value
        is MessageDeleteEvent -> event.channelId == channel.id

        is ChannelCreateEvent -> event.channel.id == channel.id
        is ChannelUpdateEvent -> event.channel.id == channel.id
        is ChannelDeleteEvent -> event.channel.id == channel.id

        is GuildCreateEvent -> event.guild.id.longValue == channel.data.guildId
        is GuildUpdateEvent -> event.guild.id.longValue == channel.data.guildId
        is GuildDeleteEvent -> event.guildId.longValue == channel.data.guildId

        else -> true
    }

}