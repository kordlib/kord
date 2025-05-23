package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class ReactionRemoveEvent(
    public override val userId: Snowflake,
    public override val channelId: Snowflake,
    public override val messageId: Snowflake,
    public override val guildId: Snowflake?,
    public override val emoji: ReactionEmoji,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : ReactionEvent {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionRemoveEvent =
        ReactionRemoveEvent(userId, channelId, messageId, guildId, emoji, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "ReactionRemoveEvent(userId=$userId, channelId=$channelId, messageId=$messageId, guildId=$guildId, emoji=$emoji, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
