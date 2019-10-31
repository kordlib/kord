package com.gitlab.kordlib.core.live

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.cache.data.ReactionData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.ReactionEmoji
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.channel.ChannelDeleteEvent
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent
import com.gitlab.kordlib.core.event.message.*
import kotlinx.coroutines.flow.Flow

@KordPreview
class LiveMessage(message: Message) : AbstractLiveEntity(), Entity by message {

    var message: Message = message
        private set

    override val events: Flow<Event>
        get() = message.kord.events

    override fun filter(event: Event): Boolean = when (event) {
        is ReactionAddEvent -> event.messageId == message.id
        is ReactionRemoveEvent -> event.messageId == message.id
        is ReactionRemoveAllEvent -> event.messageId == message.id

        is MessageCreateEvent -> event.message.id == message.id
        is MessageUpdateEvent -> event.messageId == message.id
        is MessageDeleteEvent -> event.messageId == message.id

        is ChannelDeleteEvent -> event.channel.id == message.channelId

        is GuildDeleteEvent -> event.guildId == message.guildId
        else -> true
    }

    override fun update(event: Event): Unit = when (event) {
        is ReactionAddEvent -> process(event)
        is ReactionRemoveEvent -> process(event)
        is ReactionRemoveAllEvent -> message = Message(message.data.copy(reactions = emptyList()), kord)

        is MessageCreateEvent -> message = event.message //this is kinda nonsensical, no?
        is MessageUpdateEvent -> message = Message(message.data + event.new, kord)
        is MessageDeleteEvent -> shutDown()

        is ChannelDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()
        else -> Unit
    }

    private fun process(event: ReactionAddEvent) = with(event.emoji) {
        val animated = this is ReactionEmoji.Custom && isAnimated

        val present = message.data.reactions
                ?.firstOrNull { it.emojiName == name && it.emojiId == id?.longValue }

        val reactions = when (present) {
            null -> message.data.reactions.orEmpty() + ReactionData(1, event.userId == kord.selfId, id?.longValue, name, animated)
            else -> {
                val updated = present.copy(count = present.count + 1)
                message.data.reactions.orEmpty() - present + updated
            }
        }

        message = Message(message.data.copy(reactions = reactions), kord)
    }

    private fun process(event: ReactionRemoveEvent) = with(event.emoji) {
        val present = message.data.reactions
                ?.firstOrNull { it.emojiName == name && it.emojiId == id?.longValue }

        val reactions = when (present) {
            null -> message.data.reactions
            else -> {
                val updated = present.copy(count = present.count - 1)
                message.data.reactions.orEmpty() - present + updated
            }
        }

        message = Message(message.data.copy(reactions = reactions), kord)
    }

}