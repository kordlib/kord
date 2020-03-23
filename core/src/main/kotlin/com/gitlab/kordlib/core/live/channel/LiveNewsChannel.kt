package com.gitlab.kordlib.core.live.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.channel.NewsChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.channel.NewsChannelCreateEvent
import com.gitlab.kordlib.core.event.channel.NewsChannelDeleteEvent
import com.gitlab.kordlib.core.event.channel.NewsChannelUpdateEvent
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent

@KordPreview
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Message channels are allowed to change type during their lifetime. As such, there's no guarantee that the channel will stay a NewsChannel", level = DeprecationLevel.WARNING)
fun NewsChannel.live() = LiveNewsChannel(this)

@KordPreview
@Deprecated("Message channels are allowed to change type during their lifetime. As such, there's no guarantee that the channel will stay a NewsChannel", level = DeprecationLevel.WARNING)
class LiveNewsChannel(channel: NewsChannel) : LiveChannel(), Entity by channel {

    override var channel: NewsChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is NewsChannelCreateEvent -> channel = event.channel
        is NewsChannelUpdateEvent -> channel = event.channel
        is NewsChannelDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()

        else -> Unit
    }

}