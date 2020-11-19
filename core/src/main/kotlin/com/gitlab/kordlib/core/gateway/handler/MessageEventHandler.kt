package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.common.entity.optional.*
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.cache.idEq
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.ReactionEmoji
import com.gitlab.kordlib.core.event.message.*
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet
import com.gitlab.kordlib.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class MessageEventHandler(
        kord: Kord,
        gateway: MasterGateway,
        cache: DataCache,
        coreFlow: MutableSharedFlow<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreFlow) {

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

        cache.query<ChannelData> { idEq(ChannelData::id, channelId) }.update {
            it.copy(lastMessageId = data.id.optionalSnowflake())
        }

        //get the user data only if it exists and the user isn't a webhook
        val userData = if (webhookId is OptionalSnowflake.Missing) {
            UserData.from(author).also { cache.put(it) }
        } else null

        //get the member and cache the member. We need the user, guild id and member to be present
        val member = if (userData != null && guildId is OptionalSnowflake.Missing && member !is Optional.Value) {
            val memberData = MemberData.from(author.id, guildId.value!!, member.value!!)
            cache.put(memberData)
            Member(memberData, userData, kord)
        } else null

        mentions.forEach {
            val user = UserData.from(it)
            cache.put(user)
            it.member.value?.let {
                cache.put(MemberData.from(userId = user.id, guildId = guildId.value!!, it))
            }
        }

        coreFlow.emit(MessageCreateEvent(Message(data, kord), guildId.value!!, member, shard))
    }

    private suspend fun handle(event: MessageUpdate, shard: Int) = with(event.message) {
        val query = cache.query<MessageData> { idEq(MessageData::id, id) }

        val old = query.asFlow().map { Message(it, kord) }.singleOrNull()
        query.update { it + this }

        mentions.orEmpty().forEach {
            val user = UserData.from(it)
            cache.put(user)
            it.member.value?.let {
                cache.put(MemberData.from(userId = user.id, guildId = guildId.value!!, it))
            }
        }

        coreFlow.emit(MessageUpdateEvent(id, channelId, this, old, kord, shard))
    }

    private suspend fun handle(event: MessageDelete, shard: Int) = with(event.message) {
        val query = cache.query<MessageData> { idEq(MessageData::id, id) }

        val removed = query.singleOrNull()?.let { Message(it, kord) }
        query.remove()

        coreFlow.emit(
                MessageDeleteEvent(id, channelId, guildId.value, removed, kord, shard)
        )
    }

    private suspend fun handle(event: MessageDeleteBulk, shard: Int) = with(event.messageBulk) {
        val query = cache.query<MessageData> { MessageData::id `in` ids }

        val removed = query.asFlow().map { Message(it, kord) }.toSet()
        query.remove()

        val ids = ids.asSequence().map { it }.toSet()

        coreFlow.emit(
                MessageBulkDeleteEvent(ids, removed, channelId, guildId.value, kord, shard)
        )
    }

    private suspend fun handle(event: MessageReactionAdd, shard: Int) = with(event.reaction) {
        /**
         * Reactions added will *always* have a name, the only case in which name is null is when a guild reaction
         * no longer exists (only id is kept). Reacting with a non-existing reaction *should* be impossible.
         **/
        val reaction = when (val id = emoji.id) {
            null -> ReactionEmoji.Unicode(emoji.name!!)
            else -> ReactionEmoji.Custom(id, emoji.name!!, emoji.animated.orElse(false))
        }

        cache.query<MessageData> { idEq(MessageData::id, messageId) }.update {
            val isMe = kord.selfId == event.reaction.userId

            val reactions = if (it.reactions.value.isNullOrEmpty()) {
                listOf(ReactionData.from(1, isMe, emoji))
            } else {
                val reactions = it.reactions.orEmpty()
                val reactionData = reactions.firstOrNull { reaction ->
                    if (emoji.id == null) reaction.emojiName == emoji.name
                    else reaction.emojiId == emoji.id && reaction.emojiName == emoji.name
                }

                when (reactionData) {
                    null -> reactions + ReactionData.from(1, isMe, emoji)
                    else -> (reactions - reactionData) + reactionData.copy(count = reactionData.count + 1, me = isMe)
                }
            }

            it.copy(reactions = Optional.Value(reactions))
        }

        coreFlow.emit(
                ReactionAddEvent(
                        userId,
                        channelId,
                        messageId,
                        guildId.value,
                        reaction,
                        kord,
                        shard
                )
        )
    }

    private suspend fun handle(event: MessageReactionRemove, shard: Int) = with(event.reaction) {
        /**
         * Reactions removed will *sometimes* have a name, the only case in which name is null is when a guild reaction
         * no longer exists (only id is kept). Removing a non-existing reaction *should* be possible.
         **/
        val reaction = when (val id = emoji.id) {
            null -> ReactionEmoji.Unicode(emoji.name!!)
            else -> ReactionEmoji.Custom(id, emoji.name ?: "", emoji.animated.orElse(false))
        }

        cache.query<MessageData> { idEq(MessageData::id, messageId) }.update {
            val oldReactions = it.reactions.value ?: return@update it
            if (oldReactions.isEmpty()) return@update it

            val me = kord.selfId == event.reaction.userId

            val reactionData = oldReactions.firstOrNull { reaction ->
                if (emoji.id == null) reaction.emojiName == emoji.name
                else reaction.emojiId == emoji.id && reaction.emojiName == emoji.name
            } ?: return@update it

            val reactions = when (val count = reactionData.count - 1) {
                0 -> (oldReactions - reactionData)
                else -> (oldReactions - reactionData) + reactionData.copy(count = count, me = reactionData.me xor me)
            }

            it.copy(reactions = Optional.Value(reactions))
        }

        coreFlow.emit(
                ReactionRemoveEvent(
                        userId,
                        channelId,
                        messageId,
                        guildId.value,
                        reaction,
                        kord,
                        shard
                )
        )
    }

    private suspend fun handle(event: MessageReactionRemoveAll, shard: Int) = with(event.reactions) {
        cache.query<MessageData> { idEq(MessageData::id, messageId) }.update { it.copy(reactions = Optional.Missing()) }

        coreFlow.emit(
                ReactionRemoveAllEvent(
                        channelId,
                        messageId,
                        guildId.value,
                        kord,
                        shard
                )
        )
    }

    private suspend fun handle(event: MessageReactionRemoveEmoji, shard: Int) = with(event.reaction) {
        cache.query<MessageData> { idEq(MessageData::id, messageId) }
                .update { it.copy(reactions = it.reactions.map { list -> list.filter { data -> data.emojiName != emoji.name } }) }

        val data = ReactionRemoveEmojiData.from(this)
        coreFlow.emit(ReactionRemoveEmojiEvent(data, kord, shard))
    }

}
