package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.ReactionEmoji
import com.gitlab.kordlib.core.event.message.*
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.core.toSnowflakeOrNull
import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet
import com.gitlab.kordlib.core.event.Event as CoreEvent

@OptIn(KordUnstableApi::class)
@Suppress("EXPERIMENTAL_API_USAGE")
internal class MessageEventHandler(
        kord: Kord,
        gateway: MasterGateway,
        cache: DataCache,
        coreEventChannel: SendChannel<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreEventChannel) {

    override suspend fun handle(event: Event, shard: Int) = when (event) {
        is MessageCreate -> handle(event, shard)
        is MessageUpdate -> handle(event, shard)
        is MessageDelete -> handle(event, shard)
        is MessageDeleteBulk -> handle(event, shard)
        is MessageReactionAdd -> handle(event, shard)
        is MessageReactionRemove -> handle(event, shard)
        is MessageReactionRemoveAll -> handle(event, shard)
        is MessageReactionRemoveEmoji -> handle(event, shard)
        else -> Unit
    }

    private suspend fun handle(event: MessageCreate, shard: Int) = with(event.message) {
        val data = MessageData.from(this)
        cache.put(data)

        cache.query<ChannelData> { ChannelData::id eq channelId.toLong() }.update {
            it.copy(lastMessageId = data.id)
        }

        //get the user data only if it exists and the user isn't a webhook
        val userData =  if (author != null && webhookId == null) {
            UserData.from(author!!).also { cache.put(it) }
        } else null

        //get the member and cache the member. We need the user, guild id and member to be present
        val member = if (userData != null && guildId != null && member != null) {
            val memberData = MemberData.from(author!!.id, guildId!!, member!!)
            cache.put(memberData)
            Member(memberData, userData, kord)
        } else null

        coreEventChannel.send(MessageCreateEvent(Message(data, kord), guildId.toSnowflakeOrNull(), member, shard))
    }

    private suspend fun handle(event: MessageUpdate, shard: Int) = with(event.message) {
        val query = cache.query<MessageData> { MessageData::id eq id.toLong() }

        val old = query.asFlow().map { Message(it, kord) }.singleOrNull()
        query.update { it + this }

        coreEventChannel.send(MessageUpdateEvent(Snowflake(id), Snowflake(channelId), this, old, kord, shard))
    }

    private suspend fun handle(event: MessageDelete, shard: Int) = with(event.message) {
        val query = cache.query<MessageData> { MessageData::id eq id.toLong() }

        val removed = query.singleOrNull()?.let { Message(it, kord) }
        query.remove()

        coreEventChannel.send(
                MessageDeleteEvent(Snowflake(id), Snowflake(channelId), guildId.toSnowflakeOrNull(), removed, kord, shard)
        )
    }

    private suspend fun handle(event: MessageDeleteBulk, shard: Int) = with(event.messageBulk) {
        val query = cache.query<MessageData> { MessageData::id `in` ids }

        val removed = query.asFlow().map { Message(it, kord) }.toSet()
        query.remove()

        val ids = ids.asSequence().map { Snowflake(it) }.toSet()

        coreEventChannel.send(
                MessageBulkDeleteEvent(ids, removed, Snowflake(channelId), guildId.toSnowflakeOrNull(), kord, shard)
        )
    }

    private suspend fun handle(event: MessageReactionAdd, shard: Int) = with(event.reaction) {
        /**
         * Reactions added will *always* have a name, the only case in which name is null is when a guild reaction
         * no longer exists (only id is kept). Reacting with a non-existing reaction *should* be impossible.
         **/
        val reaction = when (val id = emoji.id) {
            null -> ReactionEmoji.Unicode(emoji.name!!)
            else -> ReactionEmoji.Custom(Snowflake(id), emoji.name!!, emoji.animated ?: false)
        }

        cache.query<MessageData> { MessageData::id eq messageId.toLong() }.update {
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
                        kord,
                        shard
                )
        )
    }

    private suspend fun handle(event: MessageReactionRemove, shard: Int) = with(event.reaction) {
        /**
         * Reactions removed will *sometimes* have a name, the only case in which name is null is when a guild reaction
         * no longer exists (only id is kept). Reomving a non-existing reaction *should* be possible.
         **/
        val reaction = when (val id = emoji.id) {
            null -> ReactionEmoji.Unicode(emoji.name!!)
            else -> ReactionEmoji.Custom(Snowflake(id), emoji.name ?: "", emoji.animated ?: false)
        }

        cache.query<MessageData> { MessageData::id eq messageId.toLong() }.update {
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
                        kord,
                        shard
                )
        )
    }

    private suspend fun handle(event: MessageReactionRemoveAll, shard: Int) = with(event.reactions) {
        cache.query<MessageData> { MessageData::id eq messageId.toLong() }.update { it.copy(reactions = emptyList()) }

        coreEventChannel.send(
                ReactionRemoveAllEvent(
                        Snowflake(channelId),
                        Snowflake(messageId),
                        guildId.toSnowflakeOrNull(),
                        kord,
                        shard
                )
        )
    }

    private suspend fun handle(event: MessageReactionRemoveEmoji, shard: Int) = with(event.reaction) {
        cache.query<MessageData> { MessageData::id eq messageId.toLong() }.update { it.copy(reactions = it.reactions?.filter { it.emojiName != emoji.name }) }

        val data = ReactionRemoveEmojiData.from(this)
        coreEventChannel.send(ReactionRemoveEmojiEvent(data, kord, shard))
    }

}
