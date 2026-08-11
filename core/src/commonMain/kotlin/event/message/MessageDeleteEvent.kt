package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class MessageDeleteEvent(
    public override val messageId: Snowflake,
    public override val channelId: Snowflake,
    public override val guildId: Snowflake?,
    public override val message: Message?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : MessageChangeEvent, Strategizable {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageDeleteEvent =
        MessageDeleteEvent(messageId, channelId, guildId, message, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "MessageDeleteEvent(messageId=$messageId, channelId=$channelId, guildId=$guildId, message=$message, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
