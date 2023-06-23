package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Integration
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class IntegrationUpdateEvent(
    public val integration: Integration,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {
    public val guildId: Snowflake get() = integration.guildId
    public val guild: GuildBehavior get() = integration.guild

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): IntegrationUpdateEvent =
        IntegrationUpdateEvent(integration, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String = "IntegrationUpdateEvent(integration=$integration, kord=$kord, shard=$shard, " +
        "customContext=$customContext, supplier=$supplier)"
}
