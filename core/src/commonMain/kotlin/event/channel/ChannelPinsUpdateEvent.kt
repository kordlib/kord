package dev.kord.core.event.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.data.ChannelPinsUpdateEventData
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.datetime.Instant

/**
 * The event dispatched when a message is pinned or unpinned in a text channel.
 *
 * This event is not sent when a pinned message is deleted.
 *
 * See [Channel Pins Update](https://discord.com/developers/docs/topics/gateway-events#channel-pins-update)
 *
 * @property data The data associated with the event
 */
public class ChannelPinsUpdateEvent(
    public val data: ChannelPinsUpdateEventData,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    /**
     * The ID of the channel that triggered the event
     */
    public val channelId: Snowflake get() = data.channelId

    /**
     * The ID of the guild that triggered the event, or null
     */
    public val guildId: Snowflake? get() = data.guildId.value

    /**
     * The timestamp of the last pin, or null.
     */
    public val lastPinTimestamp: Instant? get() = data.lastPinTimestamp.value

    /**
     * The [Guild][GuildBehavior] that triggered the event, or null.
     */
    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    /**
     * The channel that triggered the event.
     */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    /**
     * Requests to get the channel for the event as type [MessageChannel]
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     * @throws EntityNotFoundException if the channel is null.
     */
    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel for the event as type [MessageChannel] or null
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     */
    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ChannelPinsUpdateEvent =
        ChannelPinsUpdateEvent(data, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "ChannelPinsUpdateEvent(channelId=$channelId, lastPinTimestamp=$lastPinTimestamp, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
