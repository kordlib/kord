package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.behavior.channel.TopGuildMessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

class WebhookUpdateEvent(
    override val guildId: Snowflake,
    val channelId: Snowflake,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    val channel: TopGuildMessageChannelBehavior get() = TopGuildMessageChannelBehavior(guildId, channelId, kord)

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getChannel(): TopGuildMessageChannel = supplier.getChannelOf(channelId)

    suspend fun getChannelOrNull(): TopGuildMessageChannel? = supplier.getChannelOfOrNull(channelId)

    suspend fun getGuild(): Guild = guild.asGuild()

    suspend fun getGuildOrNull(): Guild? = guild.asGuildOrNull()

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): WebhookUpdateEvent =
        WebhookUpdateEvent(guildId, channelId, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "WebhookUpdateEvent(guildId=$guildId, channelId=$channelId, kord=$kord, shard=$shard, supplier=$supplier)"
    }

}