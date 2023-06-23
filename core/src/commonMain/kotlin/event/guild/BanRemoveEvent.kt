package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class BanRemoveEvent(
    public val user: User,
    public val guildId: Snowflake,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = user.kord.defaultSupplier,
) : Event, Strategizable {

    override val kord: Kord get() = user.kord

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): BanRemoveEvent =
        BanRemoveEvent(user, guildId, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "BanRemoveEvent(user=$user, guildId=$guildId, shard=$shard, supplier=$supplier)"
    }
}
