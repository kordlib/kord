package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class MemberJoinEvent(
    public val member: Member,
    override val shard: Int,
    override val supplier: EntitySupplier = member.kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(member.kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    override val kord: Kord get() = member.kord

    public val guildId: Snowflake get() = member.guildId

    public val guild: GuildBehavior get() = member.guild

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MemberJoinEvent =
        MemberJoinEvent(member, shard, strategy.supply(kord))

    override fun toString(): String {
        return "MemberJoinEvent(member=$member, shard=$shard, supplier=$supplier)"
    }

}
