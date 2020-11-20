package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy

class MemberJoinEvent(
        val member: Member,
        override val shard: Int,
        override val supplier: EntitySupplier = member.kord.defaultSupplier
) : Event, Strategizable {

    override val kord: Kord get() = member.kord

    val guildId: Snowflake get() = member.guildId

    val guild: GuildBehavior get() = member.guild

    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MemberJoinEvent =
            MemberJoinEvent(member, shard, strategy.supply(kord))

    override fun toString(): String {
        return "MemberJoinEvent(member=$member, shard=$shard, supplier=$supplier)"
    }

}
