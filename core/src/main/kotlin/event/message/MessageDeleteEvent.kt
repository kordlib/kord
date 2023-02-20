package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

/**
 * The event dispatched when a message is deleted.
 *
 * See [Message delete](https://discord.com/developers/docs/resources/channel#delete-message)
 *
 * @param messageId The ID of the message that triggered the event
 * @param channelId The ID of the channel that triggered the event
 * @param guildId The ID of the guild that triggered the event. It may be `null` if the message was not stored in the cache
 * @param message The message that triggered the event. It may be `null` if the message was not stored in the cache
 */
public class MessageDeleteEvent(
    public val messageId: Snowflake,
    public val channelId: Snowflake,
    public val guildId: Snowflake?,
    public val message: Message?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {
    /**
     * The [MessageChannelBehavior] that triggered the event.
     */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    /**
     * The [GuildBehavior] that triggered the event
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
     * Requests to get the channel this message was delete from, if it was deleted in one,
     * returns `null` if the [MessageChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the guild this message was created in, if it was created in one,
     * returns null if the [Guild] isn't present or the message was a [DM][DmChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    @Deprecated(
        "Deprecated in favour of getGuildOrNull() as it provides more clarity over the functionality",
        ReplaceWith("getGuildOrNull()"),
        DeprecationLevel.WARNING
    )
    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    /**
     * Requests to get the guild this message was created in, if it was created in one,
     * returns null if the [Guild] isn't present or the message was a [DM][DmChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageDeleteEvent =
        MessageDeleteEvent(messageId, channelId, guildId, message, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "MessageDeleteEvent(messageId=$messageId, channelId=$channelId, guildId=$guildId, message=$message, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
