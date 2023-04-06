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
 * The event dispatched when a [Role] is deleted.
 *
 * See [Guild Role Delete Event](https://discord.com/developers/docs/topics/gateway-events#guild-role-delete)
 *
 * @param guildId The ID of the guild that triggered the event
 * @param roleId The ID of the role that triggered the event
 * @param role The [Role] that triggered the event. It may be `null` if it was not stored in the cache
 */
public class RoleDeleteEvent(
    public val guildId: Snowflake,
    public val roleId: Snowflake,
    public val role: Role?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {
    /**
     * The [GuildBehavior] that triggered the event.
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

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): RoleDeleteEvent =
        RoleDeleteEvent(guildId, roleId, role, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "RoleDeleteEvent(guildId=$guildId, roleId=$roleId, role=$role, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
