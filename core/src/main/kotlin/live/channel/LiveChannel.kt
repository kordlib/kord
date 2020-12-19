package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.channel.*
import dev.kord.core.event.Event
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.guild.GuildUpdateEvent
import dev.kord.core.event.message.*
import dev.kord.core.live.AbstractLiveKordEntity

@KordPreview
fun Channel.live() = when (this) {
    is DmChannel -> this.live()
    is NewsChannel -> this.live()
    is StoreChannel -> this.live()
    is TextChannel -> this.live()
    is VoiceChannel -> this.live()
    else -> error("unsupported channel type")
}

@KordPreview
abstract class LiveChannel : AbstractLiveKordEntity() {

    abstract val channel: Channel

    override fun filter(event: Event): Boolean = when (event) {
        is VoiceStateUpdateEvent -> event.state.channelId == channel.id

        is ReactionAddEvent -> event.channelId == channel.id
        is ReactionRemoveEvent -> event.channelId == channel.id
        is ReactionRemoveAllEvent -> event.channelId == channel.id

        is MessageCreateEvent -> event.message.channelId == channel.id
        is MessageUpdateEvent -> event.new.channelId == channel.id
        is MessageDeleteEvent -> event.channelId == channel.id

        is ChannelCreateEvent -> event.channel.id == channel.id
        is ChannelUpdateEvent -> event.channel.id == channel.id
        is ChannelDeleteEvent -> event.channel.id == channel.id

        is GuildCreateEvent -> event.guild.id == channel.data.guildId.value
        is GuildUpdateEvent -> event.guild.id == channel.data.guildId.value
        is GuildDeleteEvent -> event.guildId == channel.data.guildId.value

        else -> false
    }

}