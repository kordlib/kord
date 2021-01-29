package dev.kord.core.extension.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.channel.Category
import dev.kord.core.event.Event
import dev.kord.core.event.channel.CategoryCreateEvent
import dev.kord.core.event.channel.CategoryDeleteEvent
import dev.kord.core.event.channel.CategoryUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.channel.LiveCategory
import dev.kord.core.live.channel.live
import dev.kord.core.live.on

@KordPreview
inline fun Category.live(block: LiveCategory.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveCategory.create(block: suspend (CategoryCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveCategory.update(block: suspend (CategoryUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveCategory.shutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is CategoryDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveCategory.delete(block: suspend (CategoryDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveCategory.guildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)
