package dev.kord.core.event.guild

import dev.kord.common.entity.DiscordIntegration
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class IntegrationCreateEvent(
    public val integration: DiscordIntegration,
    public val guildId: Snowflake,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable =
        IntegrationCreateEvent(integration, guildId, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "IntegrationCreateEvent(integration=$integration, guildId=$guildId, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}