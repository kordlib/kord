package com.gitlab.kordlib.core.live.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.channel.Category
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.channel.CategoryCreateEvent
import com.gitlab.kordlib.core.event.channel.CategoryDeleteEvent
import com.gitlab.kordlib.core.event.channel.CategoryUpdateEvent
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent

@KordPreview
fun Category.live() = LiveCategory(this)

@KordPreview
class LiveCategory(channel: Category) : LiveChannel(), Entity by channel {

    override var channel: Category = channel
        private set

    override fun update(event: Event) = when (event) {
        is CategoryCreateEvent -> channel = event.channel
        is CategoryUpdateEvent -> channel = event.channel
        is CategoryDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()

        else -> Unit
    }

}