package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

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
