package dev.kord.core.behavior

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.cache.data.MessageData
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.rest.builder.message.MessageCreateBuilder
import dev.kord.rest.builder.message.MessageModifyBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.flow.Flow
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Message](https://discord.com/developers/docs/resources/channel#message-object).
 */
interface MessageBehavior : KordEntity, Strategizable {
    /**
     * The channel id this message belongs to.
     */
    val channelId: Snowflake

    /**
     * The channel behavior that this message belongs to.
     */
    val channel get() = MessageChannelBehavior(channelId, kord)

    /**
     * Requests to get the channel this message was send in.
     */
    suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

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
        kord.rest.channel.deleteMessage(channelId = channelId, messageId = id)
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
        kord.rest.channel.createReaction(channelId = channelId, messageId = id, emoji = emoji.urlFormat)
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
    suspend fun publish(): Message {
        val response = kord.rest.channel.crossPost(channelId = channelId, messageId = id)
        val data = MessageData.from(response)
        return Message(data, kord)
    }

    /**
     * Requests to delete an [emoji] from this message made by a [userId].
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun deleteReaction(userId: Snowflake, emoji: ReactionEmoji) {
        kord.rest.channel.deleteReaction(channelId = channelId, messageId = id, userId = userId, emoji = emoji.urlFormat)
    }

    /**
     * Requests to delete an [emoji] from this message made by this bot.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun deleteOwnReaction(emoji: ReactionEmoji) {
        kord.rest.channel.deleteOwnReaction(channelId = channelId, messageId = id, emoji = emoji.urlFormat)
    }

    /**
     * Requests to delete all reactions from this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun deleteAllReactions() {
        kord.rest.channel.deleteAllReactions(channelId = channelId, messageId = id)
    }

    /**
     * Requests to delete all [emoji] reactions from this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun deleteReaction(emoji: ReactionEmoji) {
        kord.rest.channel.deleteAllReactionsForEmoji(channelId = channelId, messageId = id, emoji = emoji.urlFormat)
    }

    /**
     * Requests to pin this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun pin() {
        kord.rest.channel.addPinnedMessage(channelId = channelId, messageId = id)
    }

    /**
     * Requests to unpin this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun unpin() {
        kord.rest.channel.deletePinnedMessage(channelId = channelId, messageId = id)
    }

    /**
     * Returns a new [MessageBehavior] with the given [strategy].
     */
    override fun withStrategy(
            strategy: EntitySupplyStrategy<*>,
    ): MessageBehavior = MessageBehavior(channelId, id, kord, strategy)

}

fun MessageBehavior(
        channelId: Snowflake,
        messageId: Snowflake,
        kord: Kord,
        strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy,
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

    override fun toString(): String {
        return "MessageBehavior(id=$id, channelId=$channelId, kord=$kord, supplier=$supplier)"
    }
}

/**
 * Requests to edit this message.
 *
 * @return The edited [Message].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun MessageBehavior.edit(builder: MessageModifyBuilder.() -> Unit): Message {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.channel.editMessage(channelId = channelId, messageId = id, builder = builder)
    val data = MessageData.from(response)

    return Message(data, kord)
}

/**
 * Request to reply to this message, setting [MessageCreateBuilder.messageReference] to this message [id][MessageBehavior.id].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun MessageBehavior.reply(builder: MessageCreateBuilder.() -> Unit): Message {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return channel.createMessage {
        builder()
        messageReference = this@reply.id
    }
}
