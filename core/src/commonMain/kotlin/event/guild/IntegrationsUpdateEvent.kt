package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The event dispatched when the integrations for a guild are updated.
 *
 * See [Guild Interactions Update](https://discord.com/developers/docs/topics/gateway-events#guild-integrations-update)
 */
public class IntegrationsUpdateEvent(
    public val guildId: Snowflake,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {
    /**
     * The [Guild] that triggered the event.
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the [Guild] that triggered the event.
     *
     * @throws [RequestException] if anything went wrong during the request
     * @throws [EntityNotFoundException] if the guild was not present
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the [Guild] that triggered the event, or `null` if the guild was not present.
     *
     * @throws [RequestException] if anything went wrong during the request
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): IntegrationsUpdateEvent =
        IntegrationsUpdateEvent(guildId, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String = "IntegrationsUpdateEvent(guildId=$guildId, kord=$kord, shard=$shard, " +
        "customContext=$customContext, supplier=$supplier)"
}
