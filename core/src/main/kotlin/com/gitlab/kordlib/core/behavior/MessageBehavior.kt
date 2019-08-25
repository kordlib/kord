package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.Pagination
import com.gitlab.kordlib.core.`object`.ReactionEmoji
import com.gitlab.kordlib.core.`object`.builder.message.MessageModifyBuilder
import com.gitlab.kordlib.core.`object`.data.MessageData
import com.gitlab.kordlib.core.`object`.data.UserData
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.rest.route.Position
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The behavior of a [Discord Message](https://discordapp.com/developers/docs/resources/channel#message-object).
 */
@ExperimentalCoroutinesApi
interface MessageBehavior : Entity {
    /**
     * The channel id this message belongs to.
     */
    val channelId: Snowflake

    /**
     * The channel behavior that this message belongs to.
     */
    val channel get() = MessageChannelBehavior(channelId, kord)

    /**
     * Requests to get this behavior as a [Channel].
     */
    suspend fun asMessage(): Message = kord.getMessage(channelId, id)!!

    /**
     * Requests to delete this message.
     */
    suspend fun delete() {
        kord.rest.channel.deleteMessage(channelId = channelId.value, messageId = id.value)
    }

    /**
     * Requests to get all users that have reacted to this message.
     */
    fun getReactors(emoji: ReactionEmoji): Flow<User> =
            Pagination.after(100, com.gitlab.kordlib.common.entity.User::id) { position: Position?, size: Int ->
                kord.rest.channel.getReactions(
                        channelId = channelId.value,
                        messageId = id.value,
                        emoji = emoji.formatted,
                        limit = size,
                        position = position
                )
            }.map { UserData.from(it) }.map { User(it, kord) }

    /**
     * Requests to add an [emoji] to this message.
     */
    suspend fun addReaction(emoji: ReactionEmoji) {
        kord.rest.channel.createReaction(channelId = channelId.value, messageId = id.value, emoji = emoji.formatted)
    }

    /**
     * Requests to add an [emoji] to this message.
     */
    suspend fun addReaction(emoji: GuildEmoji) {
        addReaction(ReactionEmoji.from(emoji))
    }

    /**
     * Requests to delete an [emoji] from this message made by a [userId].
     */
    suspend fun deleteReaction(userId: Snowflake, emoji: ReactionEmoji) {
        kord.rest.channel.deleteReaction(channelId = channelId.value, messageId = id.value, userId = userId.value, emoji = emoji.formatted)
    }

    /**
     * Requests to delete an [emoji] from this message made by this bot.
     */
    suspend fun deleteOwnReaction(emoji: ReactionEmoji) {
        kord.rest.channel.deleteOwnReaction(channelId = channelId.value, messageId = id.value, emoji = emoji.formatted)
    }

    /**
     * Requests to delete all reactions from this message.
     */
    suspend fun deleteAllReactions() {
        kord.rest.channel.deleteAllReactions(channelId = channelId.value, messageId = id.value)
    }

    /**
     * Requests to pin this message.
     */
    suspend fun pin() {
        kord.rest.channel.addPinnedMessage(channelId = channelId.value, messageId = id.value)
    }

    /**
     * Requests to unpin this message.
     */
    suspend fun unpin() {
        kord.rest.channel.deletePinnedMessage(channelId = channelId.value, messageId = id.value)
    }

    companion object {
        internal operator fun invoke(channelId: Snowflake, messageId: Snowflake, kord: Kord) = object : MessageBehavior {
            override val channelId: Snowflake = channelId
            override val id: Snowflake = messageId
            override val kord: Kord = kord
        }
    }

}

/**
 * Requests to edit this message.
 *
 * @return The edited [Message].
 */
suspend inline fun MessageBehavior.edit(builder: MessageModifyBuilder.() -> Unit): Message {
    val request = MessageModifyBuilder().apply(builder).toRequest()

    val response = kord.rest.channel.editMessage(channelId = channelId.value, messageId = id.value, message = request)
    val data = MessageData.from(response)

    return Message(data, kord)
}