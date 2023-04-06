package dev.kord.core.event.message

import dev.kord.common.entity.DiscordPartialMessage
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The event dispatched when a message is updated.
 *
 * See [Message update event](https://discord.com/developers/docs/resources/channel#edit-message)
 *
 * @param messageId The ID of the message that triggered the event
 * @param channelId The ID of the channel that the event occurred in
 * @param new The new message
 * @param old The old [Message]. May be `null` if the old message was not stored in the cache
 */
public class MessageUpdateEvent(
    public val messageId: Snowflake,
    public val channelId: Snowflake,
    public val new: DiscordPartialMessage,
    public val old: Message?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    /**
     * The behavior of the message that was updated.
     */
    public val message: MessageBehavior get() = MessageBehavior(messageId = messageId, channelId = channelId, kord = kord)

    /**
     * The behavior of the channel in which the message was updated.
     */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(id = channelId, kord = kord)

    /**
     * Requests to get the message triggering the event as a [Message]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Message] wasn't present.
     */
    public suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)

    /**
     * Requests to get the message triggering the event as a [Message].
     * Returns `null` if the [Message] wasn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageUpdateEvent =
        MessageUpdateEvent(messageId, channelId, new, old, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "MessageUpdateEvent(messageId=$messageId, channelId=$channelId, new=$new, old=$old, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
