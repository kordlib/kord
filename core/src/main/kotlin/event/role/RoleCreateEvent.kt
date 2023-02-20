package dev.kord.core.event.role

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Role
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The event dispatched when a [Role] is created.
 *
 * See [Guild Role Create Event](https://discord.com/developers/docs/topics/gateway-events#guild-role-create)
 *
 * @param role The created [Role] that triggered the event
 */
public class RoleCreateEvent(
    public val role: Role,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = role.kord.defaultSupplier,
) : Event, Strategizable {

    override val kord: Kord get() = role.kord

    /**
     * The ID of the guild that triggered the event.
     */
    public val guildId: Snowflake get() = role.guildId

    /**
     * The [GuildBehavior] tht triggered the event
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the guild triggering the event as a [Guild].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the guild is `null`
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild triggering the event as a [Guild].
     * Returns `null` if the [Guild] wasn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): RoleCreateEvent =
        RoleCreateEvent(role, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "RoleCreateEvent(role=$role, shard=$shard, supplier=$supplier)"
    }
}
