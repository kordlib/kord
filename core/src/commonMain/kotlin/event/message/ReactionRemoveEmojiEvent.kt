package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.cache.data.ReactionRemoveEmojiData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

/**
 * The event triggered when all instances reaction are removed.
 *
 * See [Reaction remove event](https://discord.com/developers/docs/topics/gateway-events#message-reaction-remove-emoji)
 *
 * @param data The data from the event
 */
public class ReactionRemoveEmojiEvent(
    public val data: ReactionRemoveEmojiData,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    /**
     * The id of the [TopGuildMessageChannel].
     */
    public val channelId: Snowflake get() = data.channelId

    /**
     * The [GuildMessageChannelBehavior] that triggered the event.
     */
    public val channel: GuildMessageChannelBehavior
        get() = GuildMessageChannelBehavior(
            guildId = guildId,
            id = channelId,
            kord = kord
        )

    /**
     * The ID of the [Guild] that triggered the event.
     */
    public val guildId: Snowflake get() = data.guildId

    /**
     * The [GuildBehavior] that triggered the event.
     */
    public val guild: GuildBehavior get() = GuildBehavior(id = guildId, kord = kord)

    /**
     * The ID of the message that triggered the event.
     */
    public val messageId: Snowflake get() = data.messageId

    /**
     * The [MessageBehavior] that triggered the event.
     */
    public val message: MessageBehavior get() = MessageBehavior(channelId = channelId, messageId = messageId, kord = kord)

    /**
     * The emoji that was removed in the event.
     */
    public val emoji: ReactionEmoji get() = ReactionEmoji.from(data.emoji)

    /**
     * Requests to get the channel triggering the event as a [TopGuildMessageChannel]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [TopGuildMessageChannel] wasn't present.
     */
    public suspend fun getChannel(): TopGuildMessageChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel triggering the event as a [TopGuildMessageChannel].
     * Returns `null` if the [TopGuildMessageChannel] wasn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): TopGuildMessageChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the guild triggering the event as a [Guild]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild triggering the event as a [Guild].
     * Returns `null` if the [Guild] wasn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to get the message triggering the event as a [Message]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Message] wasn't present.
     */
    public suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)

    /**
     * Requests to get the message triggering the event as a [Message].
     * Returns `null` if the [Message] wasn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionRemoveEmojiEvent =
        ReactionRemoveEmojiEvent(data, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "ReactionRemoveEmojiEvent(data=$data, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
