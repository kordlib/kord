package dev.kord.core.behavior

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
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.modify.UserMessageModifyBuilder
import dev.kord.rest.builder.message.modify.WebhookMessageModifyBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.flow.Flow
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Message](https://discord.com/developers/docs/resources/channel#message-object).
 */
public interface MessageBehavior : KordEntity, Strategizable {
    /**
     * The channel id this message belongs to.
     */
    public val channelId: Snowflake

    /**
     * The channel behavior that this message belongs to.
     */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    /**
     * Requests to get the channel this message was send in.
     */
    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get this behavior as a [Message].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the message wasn't present.
     */
    public suspend fun asMessage(): Message = supplier.getMessage(channelId = channelId, messageId = id)

    /**
     * Requests to get this behavior as a [Message],
     * returns null if the message isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun asMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = id)

    /**
     * Retrieve the [Message] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    public suspend fun fetchMessage(): Message = supplier.getMessage(channelId, id)


    /**
     * Retrieve the [Message] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [Message] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun fetchMessageOrNull(): Message? = supplier.getMessageOrNull(channelId, id)

    /**
     * Requests to delete this message.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete(reason: String? = null) {
        kord.rest.channel.deleteMessage(channelId = channelId, messageId = id, reason = reason)
    }

    /**
     * Requests to delete this message if it was previously sent from a [Webhook] with the given [webhookId] using the
     * [token] for authentication.
     *
     * If this message is in a thread, [threadId] must be specified.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun delete(webhookId: Snowflake, token: String, threadId: Snowflake? = null) {
        kord.rest.webhook.deleteWebhookMessage(webhookId, token, messageId = id, threadId)
    }

    /**
     * Requests to get all users that have reacted to this message.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */

    public fun getReactors(emoji: ReactionEmoji): Flow<User> =
        kord.with(EntitySupplyStrategy.rest).getReactors(channelId, id, emoji)

    /**
     * Requests to add an [emoji] to this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun addReaction(emoji: ReactionEmoji) {
        kord.rest.channel.createReaction(channelId = channelId, messageId = id, emoji = emoji.urlFormat)
    }

    /**
     * Requests to add an [emoji] to this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun addReaction(emoji: GuildEmoji) {
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

    public suspend fun publish(): Message {
        val response = kord.rest.channel.crossPost(channelId = channelId, messageId = id)
        val data = MessageData.from(response)
        return Message(data, kord)
    }

    /**
     * Requests to delete an [emoji] from this message made by a [userId].
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun deleteReaction(userId: Snowflake, emoji: ReactionEmoji) {
        kord.rest.channel.deleteReaction(
            channelId = channelId,
            messageId = id,
            userId = userId,
            emoji = emoji.urlFormat
        )
    }

    /**
     * Requests to delete an [emoji] from this message made by this bot.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun deleteOwnReaction(emoji: ReactionEmoji) {
        kord.rest.channel.deleteOwnReaction(channelId = channelId, messageId = id, emoji = emoji.urlFormat)
    }

    /**
     * Requests to delete all reactions from this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun deleteAllReactions() {
        kord.rest.channel.deleteAllReactions(channelId = channelId, messageId = id)
    }

    /**
     * Requests to delete all [emoji] reactions from this message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun deleteReaction(emoji: ReactionEmoji) {
        kord.rest.channel.deleteAllReactionsForEmoji(channelId = channelId, messageId = id, emoji = emoji.urlFormat)
    }

    /**
     * Requests to pin this message.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun pin(reason: String? = null) {
        kord.rest.channel.addPinnedMessage(channelId = channelId, messageId = id, reason = reason)
    }

    /**
     * Requests to unpin this message.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun unpin(reason: String? = null) {
        kord.rest.channel.deletePinnedMessage(channelId = channelId, messageId = id, reason)
    }

    /**
     * Returns a new [MessageBehavior] with the given [strategy].
     */
    override fun withStrategy(
        strategy: EntitySupplyStrategy<*>,
    ): MessageBehavior = MessageBehavior(channelId, id, kord, strategy)

}

public fun MessageBehavior(
    channelId: Snowflake,
    messageId: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy,
): MessageBehavior = object : MessageBehavior {
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
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun MessageBehavior.edit(builder: UserMessageModifyBuilder.() -> Unit): Message {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val response =
        kord.rest.channel.editMessage(channelId = channelId, messageId = id, builder = builder)
    val data = MessageData.from(response)

    return Message(data, kord)
}

@Deprecated(
    "'editWebhookMessage' was renamed to 'edit'",
    ReplaceWith("this.edit(webhookId, token, threadId = null) { builder() }", "dev.kord.core.behavior.edit"),
    DeprecationLevel.ERROR,
)
public suspend inline fun MessageBehavior.editWebhookMessage(
    webhookId: Snowflake,
    token: String,
    builder: WebhookMessageModifyBuilder.() -> Unit,
): Message {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return edit(webhookId, token, threadId = null, builder)
}

/**
 * Requests to edit this message if it was previously sent from a [Webhook] with the given [webhookId] using the
 * [token] for authentication.
 *
 * If this message is in a thread, [threadId] must be specified.
 *
 * @return The edited [Message].
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun MessageBehavior.edit(
    webhookId: Snowflake,
    token: String,
    threadId: Snowflake? = null,
    builder: WebhookMessageModifyBuilder.() -> Unit
): Message {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.webhook.editWebhookMessage(webhookId, token, messageId = id, threadId, builder)
    val data = MessageData.from(response)
    return Message(data, kord)
}

/**
 * Request to reply to this message, setting [messageReference][UserMessageCreateBuilder.messageReference] to this
 * message's [id][MessageBehavior.id].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun MessageBehavior.reply(builder: UserMessageCreateBuilder.() -> Unit): Message {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return channel.createMessage {
        builder()
        messageReference = this@reply.id
    }
}
