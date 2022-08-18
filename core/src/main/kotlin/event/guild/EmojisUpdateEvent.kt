package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class EmojisUpdateEvent(
    public val guildId: Snowflake,
    public val emojis: Set<GuildEmoji>,
    public val old: Set<GuildEmoji>?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EmojisUpdateEvent =
        EmojisUpdateEvent(guildId, emojis, old, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "EmojisUpdateEvent(guildId=$guildId, emojis=$emojis, old=$old, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
