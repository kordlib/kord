package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlin.coroutines.CoroutineContext

class BanRemoveEvent(
    val user: User,
    val guildId: Snowflake,
    override val shard: Int,
    override val supplier: EntitySupplier = user.kord.defaultSupplier,
    override val coroutineContext: CoroutineContext = user.kord.coroutineContext,
) : Event, Strategizable {

    override val kord: Kord get() = user.kord

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): BanRemoveEvent =
        BanRemoveEvent(user, guildId, shard, strategy.supply(kord))

    override fun toString(): String {
        return "BanRemoveEvent(user=$user, guildId=$guildId, shard=$shard, supplier=$supplier)"
    }
}