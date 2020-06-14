package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.cache.data.InviteDeleteData
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull

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
     * The [GuildChannel] of the invite.
     */
    val channelId: Snowflake get() = Snowflake(data.channelId)

    /**
     * The behavior of the [GuildChannel] of the invite.
     */
    val channel: GuildChannelBehavior get() = GuildChannelBehavior(guildId = guildId, id = channelId, kord = kord)

    /**
     * The [Guild] of the invite.
     */
    val guildId: Snowflake get() = Snowflake(data.guildId)

    /**
     * The behavior of the [Guild] of the invite.
     */
    val guild get() : GuildBehavior = GuildBehavior(id = guildId, kord = kord)

    /**
     * The unique invite code.
     */
    val code: String get() = data.code

    /**
     * Requests to get the [GuildChannel] of the invite.
     */
    suspend fun getChannel(): GuildChannel = supplier.getChannelOf(channelId)

    suspend fun getChannelOrNull(): GuildChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the [Guild] of the invite.
     */
    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InviteDeleteEvent =
            InviteDeleteEvent(data, kord, shard, strategy.supply(kord))

}