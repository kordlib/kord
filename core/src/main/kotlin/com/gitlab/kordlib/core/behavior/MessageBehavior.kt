package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.cache.data.MessageData
import com.gitlab.kordlib.core.cache.data.UserData
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.paginateForwards
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.rest.builder.message.MessageModifyBuilder
import com.gitlab.kordlib.rest.service.RestClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.common.entity.Permission

/**
 * The behavior of a [Discord Message](https://discord.com/developers/docs/resources/channel#message-object).
 */
@OptIn(KordUnstableApi::class)
interface MessageBehavior : Entity, Strategizable {
    /**
     * The channel id this message belongs to.
     */
    val channelId: Snowflake

    /**
     * The channel behavior that this message belongs to.
     */
    val channel get() = MessageChannelBehavior(channelId, kord)

    /**
     * Requests to get the this behavior as a [Message].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the message wasn't present.
     */
    suspend fun asMessage(): Message = supplier.getMessage(channelId = channelId, messageId = id)

    /**
     * Requests to get this behavior as a [Message],
     * returns null if the message isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun asMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = id)


    /**
     * Requests to delete this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete() {
        kord.rest.channel.deleteMessage(channelId = channelId.value, messageId = id.value)
    }

    /**
     * Requests to get all users that have reacted to this message.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */

    fun getReactors(emoji: ReactionEmoji): Flow<User> =
            kord.with(EntitySupplyStrategy.rest).getReactors(channelId, id, emoji)

    /**
     * Requests to add an [emoji] to this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun addReaction(emoji: ReactionEmoji) {
        kord.rest.channel.createReaction(channelId = channelId.value, messageId = id.value, emoji = emoji.urlFormat)
    }

    /**
     * Requests to add an [emoji] to this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun addReaction(emoji: GuildEmoji) {
        addReaction(ReactionEmoji.from(emoji))
    }

    /**
     * Requests to publish this message to following channels, this function assumes the message was created
     * in an announcement channel.
     *
     * Requires the [Permission.SendMessages] permission if the bot created the message,
     * or the [Permission.ManageChannels] permission otherwise.
     *
     * @return The updated message after publishing.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    @KordPreview
    suspend fun publish() : Message {
        val response = kord.rest.channel.crossPost(channelId =  channelId.value, messageId = id.value)
        val data = MessageData.from(response)
        return Message(data, kord)
    }

    /**
     * Requests to delete an [emoji] from this message made by a [userId].
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun deleteReaction(userId: Snowflake, emoji: ReactionEmoji) {
        kord.rest.channel.deleteReaction(channelId = channelId.value, messageId = id.value, userId = userId.value, emoji = emoji.urlFormat)
    }

    /**
     * Requests to delete an [emoji] from this message made by this bot.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun deleteOwnReaction(emoji: ReactionEmoji) {
        kord.rest.channel.deleteOwnReaction(channelId = channelId.value, messageId = id.value, emoji = emoji.urlFormat)
    }

    /**
     * Requests to delete all reactions from this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun deleteAllReactions() {
        kord.rest.channel.deleteAllReactions(channelId = channelId.value, messageId = id.value)
    }

    /**
     * Requests to delete all [emoji] reactions from this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun deleteReaction(emoji: ReactionEmoji) {
        kord.rest.channel.deleteAllReactionsForEmoji(channelId = channelId.value, messageId = id.value, emoji = emoji.urlFormat)
    }

    /**
     * Requests to pin this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun pin() {
        kord.rest.channel.addPinnedMessage(channelId = channelId.value, messageId = id.value)
    }

    /**
     * Requests to unpin this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun unpin() {
        kord.rest.channel.deletePinnedMessage(channelId = channelId.value, messageId = id.value)
    }

    /**
     * Returns a new [MessageBehavior] with the given [strategy].
     */
    override fun withStrategy(
            strategy: EntitySupplyStrategy<*>
    ): MessageBehavior = MessageBehavior(channelId, id, kord, strategy)

    companion object {
        internal operator fun invoke(
                channelId: Snowflake,
                messageId: Snowflake,
                kord: Kord, strategy:
                EntitySupplyStrategy<*> = kord.resources.defaultStrategy
        ) = object : MessageBehavior {
            override val channelId: Snowflake = channelId
            override val id: Snowflake = messageId
            override val kord: Kord = kord
            override val supplier: EntitySupplier = strategy.supply(kord)

            override fun hashCode(): Int = Objects.hash(id)

            override fun equals(other: Any?): Boolean = when (other) {
                is MessageBehavior -> other.id == id && other.channelId == channelId
                else -> false
            }
        }
    }

}

/**
 * Requests to edit this message.
 *
 * @return The edited [Message].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(KordUnstableApi::class)
suspend inline fun MessageBehavior.edit(builder: MessageModifyBuilder.() -> Unit): Message {
    val response = kord.rest.channel.editMessage(channelId = channelId.value, messageId = id.value, builder = builder)
    val data = MessageData.from(response)

    return Message(data, kord)
}