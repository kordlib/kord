package com.gitlab.kordlib.core.live.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.channel.TextChannelCreateEvent
import com.gitlab.kordlib.core.event.channel.TextChannelDeleteEvent
import com.gitlab.kordlib.core.event.channel.TextChannelUpdateEvent
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent

@KordPreview
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Message channels are allowed to change type during their lifetime. As such, there's no guarantee that the channel will stay a TextChannel", level = DeprecationLevel.WARNING)
fun TextChannel.live() = LiveTextChannel(this)

@KordPreview
@Deprecated("Message channels are allowed to change type during their lifetime. As such, there's no guarantee that the channel will stay a TextChannel", level = DeprecationLevel.WARNING)
class LiveTextChannel(channel: TextChannel) : LiveChannel(), Entity by channel {

    override var channel: TextChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is TextChannelCreateEvent -> channel = event.channel
        is TextChannelUpdateEvent -> channel = event.channel
        is TextChannelDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()

        else -> Unit
    }

}