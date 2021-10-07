package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.TopGuildChannelBehavior
import dev.kord.core.cache.data.InviteDeleteData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * Sent when an invite is deleted.
 */
public class InviteDeleteEvent(
    public val data: InviteDeleteData,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    /**
     * The [TopGuildChannel] of the invite.
     */
    public val channelId: Snowflake get() = data.channelId

    /**
     * The behavior of the [TopGuildChannel] of the invite.
     */
    public val channel: TopGuildChannelBehavior get() = TopGuildChannelBehavior(guildId = guildId, id = channelId, kord = kord)

    /**
     * The [Guild] of the invite.
     */
    public val guildId: Snowflake get() = data.guildId

    /**
     * The behavior of the [Guild] of the invite.
     */
    public val guild: GuildBehavior get() : GuildBehavior = GuildBehavior(id = guildId, kord = kord)

    /**
     * The unique invite code.
     */
    public val code: String get() = data.code

    /**
     * Requests to get the [TopGuildChannel] of the invite.
     */
    public suspend fun getChannel(): TopGuildChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): TopGuildChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the [Guild] of the invite.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InviteDeleteEvent =
        InviteDeleteEvent(data, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "InviteDeleteEvent(data=$data, kord=$kord, shard=$shard, supplier=$supplier)"
    }

}
