package dev.kord.core.event.role

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Role
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlin.coroutines.CoroutineContext

class RoleUpdateEvent(
    val role: Role,
    override val shard: Int,
    override val supplier: EntitySupplier = role.kord.defaultSupplier,
    override val coroutineContext: CoroutineContext = role.kord.coroutineContext,
) : Event, Strategizable {

    override val kord: Kord get() = role.kord

    val guildId: Snowflake get() = role.guildId

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): RoleUpdateEvent =
        RoleUpdateEvent(role, shard, strategy.supply(kord))

    override fun toString(): String {
        return "RoleUpdateEvent(role=$role, shard=$shard, supplier=$supplier)"
    }
}
