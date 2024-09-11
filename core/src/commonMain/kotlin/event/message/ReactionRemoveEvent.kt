package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

/**
 * The event dispatched when a reaction is removed from a message.
 *
 * See [Reaction remove event](https://discord.com/developers/docs/topics/gateway-events#message-reaction-remove)
 *
 * @param userId The ID of the user that triggered the event
 * @param channelId THe ID of the channel that triggered the event
 * @param messageId The ID of the message that triggered the event
 * @param guildId The ID of the guild that triggered the event. It may be `null` if it was not stored in the cache
 * @param emoji The emoji removed from the message
 */
public class ReactionRemoveEvent(
    public val userId: Snowflake,
    public val channelId: Snowflake,
    public val messageId: Snowflake,
    public val guildId: Snowflake?,
    public val emoji: ReactionEmoji,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {
    /**
     * The [MessageChannelBehavior] that triggered the event
     */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    /**
     * The [GuildBehavior] that triggered the event
     */
    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    /**
     * The [MessageBehavior] that triggered the event
     */
    public val message: MessageBehavior get() = MessageBehavior(channelId, messageId, kord)

    /**
     * The [UserBehavior] that triggered the event
     */
    public val user: UserBehavior get() = UserBehavior(userId, kord)

    /**
     * The [MemberBehavior] that triggered the event
     */
    public val userAsMember: MemberBehavior? get() = guildId?.let { MemberBehavior(it, userId, kord) }

    /**
     * Requests to get the channel triggering the event as a [MessageChannel]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [MessageChannel] wasn't present.
     */
    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel triggering the event as a [MessageChannel].
     * Returns `null` if the [MessageChannel] wasn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the guild triggering the event as a [Guild].
     * Returns `null` if the [Guild] wasn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

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

    /**
     * Requests to get the user triggering the event as a [User]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [User] wasn't present.
     */
    public suspend fun getUser(): User = supplier.getUser(userId)

    /**
     * Requests to get the user triggering the event as a [User].
     * Returns `null` if the [TopGuildMessageChannel] wasn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)

    /**
     * Requests to get the user triggering the event as a [Member].
     * Returns `null` if the [TopGuildMessageChannel] wasn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getUserAsMember(): Member? =
        guildId?.let { supplier.getMemberOrNull(guildId = guildId, userId = userId) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionRemoveEvent =
        ReactionRemoveEvent(userId, channelId, messageId, guildId, emoji, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "ReactionRemoveEvent(userId=$userId, channelId=$channelId, messageId=$messageId, guildId=$guildId, emoji=$emoji, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
