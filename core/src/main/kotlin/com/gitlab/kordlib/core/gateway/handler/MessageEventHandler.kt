package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.cache.data.MessageData
import com.gitlab.kordlib.core.cache.data.ReactionData
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.ReactionEmoji
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.event.message.*
import com.gitlab.kordlib.core.toSnowflakeOrNull
import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet
import com.gitlab.kordlib.core.event.Event as CoreEvent
import kotlinx.coroutines.channels.Channel as CoroutineChannel

@Suppress("EXPERIMENTAL_API_USAGE")
internal class MessageEventHandler(
        kord: Kord,
        gateway: Gateway,
        cache: DataCache,
        coreEventChannel: SendChannel<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreEventChannel) {

    override suspend fun handle(event: Event) = when (event) {
        is MessageCreate -> handle(event)
        is MessageUpdate -> handle(event)
        is MessageDelete -> handle(event)
        is MessageDeleteBulk -> handle(event)
        is MessageReactionAdd -> handle(event)
        is MessageReactionRemove -> handle(event)
        is MessageReactionRemoveAll -> handle(event)
        else -> Unit
    }

    private suspend fun handle(event: MessageCreate) = with(event.message) {
        val data = MessageData.from(this)
        cache.put(data)

        cache.find<ChannelData> { ChannelData::id eq channelId }.update {
            it.copy(lastMessageId = data.id)
        }

        coreEventChannel.send(MessageCreateEvent(Message(data, kord)))
    }

    private suspend fun handle(event: MessageUpdate) = with(event.message) {
        val query = cache.find<MessageData> { MessageData::id eq id }

        val old = query.asFlow().map { Message(it, kord) }.singleOrNull()
        query.update { it + this }

        coreEventChannel.send(MessageUpdateEvent(Snowflake(id), Snowflake(channelId), old, kord))
    }

    private suspend fun handle(event: MessageDelete) = with(event.message) {
        val query = cache.find<MessageData> { MessageData::id eq id }

        val removed = query.singleOrNull()?.let { Message(it, kord) }
        query.remove()

        coreEventChannel.send(
                MessageDeleteEvent(Snowflake(id), Snowflake(channelId), guildId.toSnowflakeOrNull(), removed, kord)
        )
    }

    private suspend fun handle(event: MessageDeleteBulk) = with(event.messageBulk) {
        val query = cache.find<MessageData> { MessageData::id `in` ids }

        val removed = query.asFlow().map { Message(it, kord) }.toSet()
        query.remove()

        val ids = ids.asSequence().map { Snowflake(it) }.toSet()

        coreEventChannel.send(
                MessageBulkDeleteEvent(ids, removed, Snowflake(channelId), guildId.toSnowflakeOrNull(), kord)
        )
    }

    private suspend fun handle(event: MessageReactionAdd) = with(event.reaction) {
        val reaction = when (val id = emoji.id) {
            null -> ReactionEmoji.Unicode(emoji.name)
            else -> ReactionEmoji.Custom(Snowflake(id), emoji.name, emoji.animated ?: false)
        }

        cache.find<MessageData> { MessageData::id eq messageId }.update {
            val isMe = kord.selfId.value == event.reaction.userId

            val reactions = if (it.reactions.isNullOrEmpty()) {
                listOf(ReactionData.from(1, isMe, emoji))
            } else {
                val reactions = it.reactions.orEmpty()
                val reaction = reactions.firstOrNull { reaction ->
                    if (emoji.id == null) reaction.emojiName == emoji.name
                    else reaction.emojiId?.toString() == emoji.id && reaction.emojiName == emoji.name
                }

                when (reaction) {
                    null -> reactions + ReactionData.from(1, isMe, emoji)
                    else -> (reactions - reaction) + reaction.copy(count = reaction.count + 1, me = isMe)
                }
            }

            it.copy(reactions = reactions)
        }

        coreEventChannel.send(
                ReactionAddEvent(
                        Snowflake(userId),
                        Snowflake(channelId),
                        Snowflake(messageId),
                        guildId.toSnowflakeOrNull(),
                        reaction,
                        kord
                )
        )
    }

    private suspend fun handle(event: MessageReactionRemove) = with(event.reaction) {
        val reaction = when (val id = emoji.id) {
            null -> ReactionEmoji.Unicode(emoji.name)
            else -> ReactionEmoji.Custom(Snowflake(id), emoji.name, emoji.animated ?: false)
        }

        cache.find<MessageData> { MessageData::id eq messageId }.update {
            if (it.reactions.isNullOrEmpty()) return@update it

            val me = kord.selfId.value == event.reaction.userId

            val oldReactions = it.reactions.orEmpty()
            val reaction = oldReactions.firstOrNull { reaction ->
                if (emoji.id == null) reaction.emojiName == emoji.name
                else reaction.emojiId?.toString() == emoji.id && reaction.emojiName == emoji.name
            } ?: return@update it

            val reactions = when (val count = reaction.count - 1) {
                0 -> (oldReactions - reaction)
                else -> (oldReactions - reaction) + reaction.copy(count = count, me = reaction.me xor me)
            }

            it.copy(reactions = reactions)
        }

        coreEventChannel.send(
                ReactionRemoveEvent(
                        Snowflake(userId),
                        Snowflake(channelId),
                        Snowflake(messageId),
                        guildId.toSnowflakeOrNull(),
                        reaction,
                        kord
                )
        )
    }

    private suspend fun handle(event: MessageReactionRemoveAll) = with(event.reactions) {
        cache.find<MessageData> { MessageData::id eq messageId }.update { it.copy(reactions = emptyList()) }

        coreEventChannel.send(
                ReactionRemoveAllEvent(
                        Snowflake(channelId),
                        Snowflake(messageId),
                        guildId.toSnowflakeOrNull(),
                        kord
                )
        )
    }

}