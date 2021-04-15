package dev.kord.core.event.message

import dev.kord.common.entity.DiscordPartialMessage
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

class MessageUpdateEvent(
    val messageId: Snowflake,
    val channelId: Snowflake,
    val new: DiscordPartialMessage,
    val old: Message?,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    /**
     * The behavior of the message that was updated.
     */
    val message: MessageBehavior get() = MessageBehavior(messageId = messageId, channelId = channelId, kord = kord)

    /**
     * The behavior of the channel in which the message was updated.
     */
    val channel: MessageChannelBehavior get() = MessageChannelBehavior(id = channelId, kord = kord)


    suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)

    suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageUpdateEvent =
        MessageUpdateEvent(messageId, channelId, new, old, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "MessageUpdateEvent(messageId=$messageId, channelId=$channelId, new=$new, old=$old, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
