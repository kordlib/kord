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
import dev.kord.core.event.kordCoroutineScope
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.core.toInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Instant
import kotlin.coroutines.CoroutineContext

public class TypingStartEvent(
    public val data: TypingStartEventData,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    public val channelId: Snowflake get() = data.channelId

    public val userId: Snowflake get() = data.userId

    public val guildId: Snowflake? get() = data.guildId.value

    public val started: Instant get() = data.timestamp.toInstant()

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

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
    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable =
        TypingStartEvent(
            data,
            kord,
            shard,
            supplier
        )

    override fun toString(): String {
        return "TypingStartEvent(channelId=$channelId, userId=$userId, guildId=$guildId, started=$started, kord=$kord, shard=$shard, supplier=$supplier)"
    }

}
