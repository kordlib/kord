package dev.kord.core.event.role

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Role
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class RoleUpdateEvent(
    public val role: Role,
    public val old: Role?,
    override val shard: Int,
    override val supplier: EntitySupplier = role.kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(role.kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    override val kord: Kord get() = role.kord

    public val guildId: Snowflake get() = role.guildId

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): RoleUpdateEvent =
        RoleUpdateEvent(role, old, shard, strategy.supply(kord))

    override fun toString(): String {
        return "RoleUpdateEvent(role=$role, old=$old, shard=$shard, supplier=$supplier)"
    }
}
