package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

/**
 * The event dispatched when messages are deleted in bulk from a channel.
 *
 * See [Message bulk delete event](https://discord.com/developers/docs/resources/channel#bulk-delete-messages)
 *
 * @param messageIds The [Set] of message Ids to delete
 * @param messages The [Set] of messages to delete as [Message]s
 * @param channelId The channel to delete them from.
 * @param guildId The guild the event occurred in
 */
public class MessageBulkDeleteEvent(
    public val messageIds: Set<Snowflake>,
    public val messages: Set<Message>,
    public val channelId: Snowflake,
    public val guildId: Snowflake?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {
    /**
     * The [MessageChannelBehavior] of the channel that triggered this event.
     */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    /**
     * The [GuildBehavior] that triggered this event
     */
    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    /**
     * Requests to get the channel triggering the event as a [MessageChannel]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [MessageChannel] wasn't present.
     */
    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel triggering the event as a [MessageChannel].
     * Returns `null` if the channel is not present
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the channel triggering the event.
     * Returns `null` if the guild is not present
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageBulkDeleteEvent =
        MessageBulkDeleteEvent(messageIds, messages, channelId, guildId, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "MessageBulkDeleteEvent(messageIds=$messageIds, messages=$messages, channelId=$channelId, guildId=$guildId, kord=$kord, shard=$shard, supplier=$supplier)"
    }

}
