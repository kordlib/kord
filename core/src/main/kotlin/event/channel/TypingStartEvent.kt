package dev.kord.core.event.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.data.TypingStartEventData
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.datetime.Instant

/**
 * The event that is dispatched when a user starts typing in a channel.
 *
 * @property data The data associated with the event
 *
 * See [Typing Start](https://discord.com/developers/docs/topics/gateway-events#typing-start)
 */
public class TypingStartEvent(
    public val data: TypingStartEventData,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    /**
     * The ID of the channel that triggered the event.
     */
    public val channelId: Snowflake get() = data.channelId

    /**
     * The ID of the user that triggered the event.
     */
    public val userId: Snowflake get() = data.userId

    /**
     * The ID of the guild that triggered the event.
     */
    public val guildId: Snowflake? get() = data.guildId.value

    /**
     * The [Instant] this event was triggered.
     */
    public val started: Instant get() = data.timestamp

    /**
     * The channel that triggered the event.
     */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    /**
     * The [Guild][GuildBehavior] that triggered the event.
     */
    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    /**
     * The [User][UserBehavior] that triggered the event.
     */
    public val user: UserBehavior get() = UserBehavior(userId, kord)

    /**
     * Requests to get the channel triggering the event.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [MessageChannel] wasn't present.
     */
    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel triggering the event,
     * returns null if the [MessageChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the user triggering the event.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [User] wasn't present.
     */
    public suspend fun getUser(): User = supplier.getUser(userId)

    /**
     * Requests to get the user triggering the event,
     * returns null if the [User] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)

    /**
     * Requests to get the guild this event was triggered in,
     * returns null if the [Guild] isn't present.
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
     * Requests to get the guild this event was triggered in,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    /**
     * Returns a copy of this class with a new [supplier] provided by the [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TypingStartEvent =
        TypingStartEvent(data, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "TypingStartEvent(channelId=$channelId, userId=$userId, guildId=$guildId, started=$started, kord=$kord, shard=$shard, supplier=$supplier)"
    }

}
