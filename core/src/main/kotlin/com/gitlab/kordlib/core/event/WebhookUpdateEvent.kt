package com.gitlab.kordlib.core.event

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildMessageChannelBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull

class WebhookUpdateEvent(
        val guildId: Snowflake,
        val channelId: Snowflake,
        override val kord: Kord,
        override val shard: Int,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    val channel: GuildMessageChannelBehavior get() = GuildMessageChannelBehavior(guildId, channelId, kord)

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getChannel(): GuildMessageChannel = supplier.getChannelOf(channelId)

    suspend fun getChannelOrNull(): GuildMessageChannel? = supplier.getChannelOfOrNull(channelId)

    suspend fun getGuild(): Guild = guild.asGuild()

    suspend fun getGuildOrNull(): Guild? = guild.asGuildOrNull()

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): WebhookUpdateEvent =
            WebhookUpdateEvent(guildId, channelId, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "WebhookUpdateEvent(guildId=$guildId, channelId=$channelId, kord=$kord, shard=$shard, supplier=$supplier)"
    }

}