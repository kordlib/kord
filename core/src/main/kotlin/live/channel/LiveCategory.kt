package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.channel.Category
import dev.kord.core.event.Event
import dev.kord.core.event.channel.CategoryCreateEvent
import dev.kord.core.event.channel.CategoryDeleteEvent
import dev.kord.core.event.channel.CategoryUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.on

@KordPreview
fun Category.live() = LiveCategory(this)

@KordPreview
inline fun Category.live(block: LiveCategory.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveCategory.onCreate(block: suspend (CategoryCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveCategory.onUpdate(block: suspend (CategoryUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveCategory.onShutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is CategoryDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveCategory.onDelete(block: suspend (CategoryDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveCategory.onGuildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveCategory(channel: Category) : LiveChannel(), KordEntity by channel {

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
