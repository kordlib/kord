package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.common.entity.optional.*
import dev.kord.core.Kord
import dev.kord.core.cache.data.*
import dev.kord.core.cache.idEq
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.message.*
import dev.kord.gateway.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet

internal class MessageEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(
        event: Event,
        shard: Int,
        kord: Kord,
    ): dev.kord.core.event.Event? = when (event) {
        is MessageCreate -> handle(event, shard, kord)
        is MessageUpdate -> handle(event, shard, kord)
        is MessageDelete -> handle(event, shard, kord)
        is MessageDeleteBulk -> handle(event, shard, kord)
        is MessageReactionAdd -> handle(event, shard, kord)
        is MessageReactionRemove -> handle(event, shard, kord)
        is MessageReactionRemoveAll -> handle(event, shard, kord)
        is MessageReactionRemoveEmoji -> handle(event, shard, kord)
        else -> null
    }

    private suspend fun handle(
        event: MessageCreate,
        shard: Int,
        kord: Kord,
    ): MessageCreateEvent = with(event.message) {
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
        val member = if (userData != null && guildId is OptionalSnowflake.Value && member is Optional.Value) {
            val memberData = MemberData.from(author.id, guildId.value!!, member.value!!)
            cache.put(memberData)
            Member(memberData, userData, kord)
        } else null

        //cache interaction user if present.
        if (interaction is Optional.Value) {
            val interactionUserData = UserData.from(interaction.value!!.user)
            cache.put(interactionUserData)
        }

        mentions.forEach {
            val user = UserData.from(it)
            cache.put(user)
            it.member.value?.let {
                cache.put(MemberData.from(userId = user.id, guildId = guildId.value!!, it))
            }
        }

        MessageCreateEvent(Message(data, kord), guildId.value, member, shard)
    }

    private suspend fun handle(
        event: MessageUpdate,
        shard: Int,
        kord: Kord,
    ): MessageUpdateEvent = with(event.message) {
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

        MessageUpdateEvent(id, channelId, this, old, kord, shard)
    }

    private suspend fun handle(
        event: MessageDelete,
        shard: Int,
        kord: Kord,
    ): MessageDeleteEvent = with(event.message) {
        val query = cache.query<MessageData> { idEq(MessageData::id, id) }

        val removed = query.singleOrNull()?.let { Message(it, kord) }
        query.remove()

        MessageDeleteEvent(id, channelId, guildId.value, removed, kord, shard)
    }

    private suspend fun handle(
        event: MessageDeleteBulk,
        shard: Int,
        kord: Kord,
    ): MessageBulkDeleteEvent =
        with(event.messageBulk) {
            val query = cache.query<MessageData> { MessageData::id `in` ids }

            val removed = query.asFlow().map { Message(it, kord) }.toSet()
            query.remove()

            val ids = ids.asSequence().map { it }.toSet()

            MessageBulkDeleteEvent(
                ids,
                removed,
                channelId,
                guildId.value,
                kord,
                shard,
            )
        }

    private suspend fun handle(
        event: MessageReactionAdd,
        shard: Int,
        kord: Kord,
    ): ReactionAddEvent =
        with(event.reaction) {
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
                        else -> (reactions - reactionData) + reactionData.copy(
                            count = reactionData.count + 1,
                            me = isMe
                        )
                    }
                }

                it.copy(reactions = Optional.Value(reactions))
            }

            ReactionAddEvent(
                userId,
                channelId,
                messageId,
                guildId.value,
                reaction,
                kord,
                shard,
            )
        }

    private suspend fun handle(
        event: MessageReactionRemove,
        shard: Int,
        kord: Kord,
    ): ReactionRemoveEvent =
        with(event.reaction) {
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
                    else -> (oldReactions - reactionData) + reactionData.copy(
                        count = count,
                        me = reactionData.me xor me
                    )
                }

                it.copy(reactions = Optional.Value(reactions))
            }

            ReactionRemoveEvent(
                userId,
                channelId,
                messageId,
                guildId.value,
                reaction,
                kord,
                shard,
            )
        }

    private suspend fun handle(
        event: MessageReactionRemoveAll,
        shard: Int,
        kord: Kord,
    ): ReactionRemoveAllEvent =
        with(event.reactions) {
            cache.query<MessageData> { idEq(MessageData::id, messageId) }
                .update { it.copy(reactions = Optional.Missing()) }

            ReactionRemoveAllEvent(
                channelId,
                messageId,
                guildId.value,
                kord,
                shard,
            )
        }

    private suspend fun handle(
        event: MessageReactionRemoveEmoji,
        shard: Int,
        kord: Kord,
    ): ReactionRemoveEmojiEvent =
        with(event.reaction) {
            cache.query<MessageData> { idEq(MessageData::id, messageId) }
                .update { it.copy(reactions = it.reactions.map { list -> list.filter { data -> data.emojiName != emoji.name } }) }

            val data = ReactionRemoveEmojiData.from(this)
            ReactionRemoveEmojiEvent(data, kord, shard)
        }

}
