package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

public class MessageBulkDeleteEvent(
    public val messageIds: Set<Snowflake>,
    public val messages: Set<Message>,
    public override val channelId: Snowflake,
    public override val guildId: Snowflake?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : MessagesChangeEvent, Strategizable {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageBulkDeleteEvent =
        MessageBulkDeleteEvent(messageIds, messages, channelId, guildId, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "MessageBulkDeleteEvent(messageIds=$messageIds, messages=$messages, channelId=$channelId, guildId=$guildId, kord=$kord, shard=$shard, supplier=$supplier)"
    }

}
