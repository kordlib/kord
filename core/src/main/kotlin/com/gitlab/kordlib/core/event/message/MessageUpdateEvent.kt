package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.common.entity.DiscordPartialMessage
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy

class MessageUpdateEvent (
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
