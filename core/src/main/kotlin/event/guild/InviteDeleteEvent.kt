package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.TopGuildChannelBehavior
import dev.kord.core.cache.data.InviteDeleteData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

/**
 * Sent when an invite is deleted.
 */
class InviteDeleteEvent(
    val data: InviteDeleteData,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    /**
     * The [TopGuildChannel] of the invite.
     */
    val channelId: Snowflake get() = data.channelId

    /**
     * The behavior of the [TopGuildChannel] of the invite.
     */
    val channel: TopGuildChannelBehavior get() = TopGuildChannelBehavior(guildId = guildId, id = channelId, kord = kord)

    /**
     * The [Guild] of the invite.
     */
    override val guildId: Snowflake get() = data.guildId

    /**
     * The behavior of the [Guild] of the invite.
     */
    val guild get() : GuildBehavior = GuildBehavior(id = guildId, kord = kord)

    /**
     * The unique invite code.
     */
    val code: String get() = data.code

    /**
     * Requests to get the [TopGuildChannel] of the invite.
     */
    suspend fun getChannel(): TopGuildChannel = supplier.getChannelOf(channelId)

    suspend fun getChannelOrNull(): TopGuildChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the [Guild] of the invite.
     */
    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InviteDeleteEvent =
        InviteDeleteEvent(data, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "InviteDeleteEvent(data=$data, kord=$kord, shard=$shard, supplier=$supplier)"
    }

}