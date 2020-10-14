package com.gitlab.kordlib.core.event.role

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Role
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy

class RoleUpdateEvent(
        val role: Role,
        override val shard: Int,
        override val supplier: EntitySupplier = role.kord.defaultSupplier
) : Event, Strategizable {

    override val kord: Kord get() = role.kord

    val guildId: Snowflake get() = role.guildId

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull():Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): RoleUpdateEvent =
            RoleUpdateEvent(role, shard, strategy.supply(kord))

    override fun toString(): String {
        return "RoleUpdateEvent(role=$role, shard=$shard, supplier=$supplier)"
    }
}
