package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.TopGuildMessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

/**
 * The event dispatched when a guild channel's webhook is created, update, or deleted.
 *
 * See [Webhooks update](https://discord.com/developers/docs/topics/gateway-events#webhooks-update)
 */
public class WebhookUpdateEvent(
    public val guildId: Snowflake,
    public val channelId: Snowflake,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {
    /**
     * The [channel][TopGuildMessageChannel] that triggered this event.
     */
    public val channel: TopGuildMessageChannelBehavior get() = TopGuildMessageChannelBehavior(guildId, channelId, kord)

    /**
     * The [Guild] that triggered this event.
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the [TopGuildMessageChannel] that triggered the event.
     *
     * @throws [RequestException] if anything went wrong during the request
     * @throws [EntityNotFoundException] if the channel was not present
     */
    public suspend fun getChannel(): TopGuildMessageChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the [TopGuildMessageChannel] that triggered the event, or `null` if the channel was not present
     *
     * @throws [RequestException] if anything went wrong during the request
     */
    public suspend fun getChannelOrNull(): TopGuildMessageChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the [Guild] that triggered the event.
     *
     * @throws [RequestException] if anything went wrong during the request
     * @throws [EntityNotFoundException] if the guild was not present
     */
    public suspend fun getGuild(): Guild = guild.asGuild()

    /**
     * Requests to get the [Guild] that triggered the event, or `null` if the guild was not present
     *
     * @throws [RequestException] if anything went wrong during the request
     */
    public suspend fun getGuildOrNull(): Guild? = guild.asGuildOrNull()

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): WebhookUpdateEvent =
        WebhookUpdateEvent(guildId, channelId, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "WebhookUpdateEvent(guildId=$guildId, channelId=$channelId, kord=$kord, shard=$shard, supplier=$supplier)"
    }

}
