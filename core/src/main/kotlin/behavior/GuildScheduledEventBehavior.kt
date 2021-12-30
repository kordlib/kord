package dev.kord.core.behavior

import dev.kord.common.entity.DiscordGuildScheduledEvent
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.GuildScheduledEvent
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.User
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.scheduled_events.ScheduledEventModifyBuilder
import dev.kord.rest.service.modifyScheduledEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Behavior of a [Discord Scheduled Guild Event](ADD LINK).
 */
public interface GuildScheduledEventBehavior : KordEntity, Strategizable {
    public val guildId: Snowflake

    /**
     * Requests all the users which are interested in this event.
     *
     * @throws RequestException if anything goes wrong during the request
     */
    public val users: Flow<User> get() = flow {
        kord.rest.guild.getScheduledEventUsers(guildId, id).users.forEach {
            val userData = UserData.from(it)
            emit(User(userData, kord, supplier))
        }
    }

    /**
     * Deletes this event.
     *
     * @throws RequestException if anything goes wrong during the request
     */
    public suspend fun delete(): Unit = kord.rest.guild.deleteScheduledEvent(guildId, id)

    /**
     * Requests to get this behavior as a [GuildScheduledEvent].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the event wasn't present.
     */
    public suspend fun asGuildScheduledEvent(): GuildScheduledEvent = supplier.getGuildScheduledEvent(guildId, id)

    /**
     * Requests to get this behavior as a [Guild],
     * returns null if the event isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun asGuildScheduledEventOrNull(): GuildScheduledEvent? = supplier.getGuildScheduledEventOrNull(guildId, id)

    /**
     * Fetches to get this behavior as a [GuildScheduledEvent].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the event wasn't present.
     */
    public suspend fun fetchGuildScheduledEvent(): GuildScheduledEvent = supplier.getGuildScheduledEvent(guildId, id)

    /**
     * Fetches to get the this behavior as a [Guild],
     * returns null if the event isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun fetchGuildScheduledEventOrNull(): GuildScheduledEvent? =
        supplier.getGuildScheduledEventOrNull(guildId, id)
}

internal fun GuildScheduledEventBehavior(
    id: Snowflake,
    guildId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): GuildScheduledEventBehavior = object : GuildScheduledEventBehavior {
    override val kord: Kord = kord
    override val id: Snowflake = id
    override val guildId: Snowflake = guildId
    override val supplier: EntitySupplier = supplier

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable =
        GuildScheduledEventBehavior(id, guildId, kord, strategy.supply(kord))
}

/**
 * Requests to modify this event according to the specified [builder].
 *
 * @throws RequestException if anything goes wrong during the request
 */
public suspend inline fun GuildScheduledEventBehavior.edit(builder: ScheduledEventModifyBuilder.() -> Unit): DiscordGuildScheduledEvent {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return kord.rest.guild.modifyScheduledEvent(guildId, id, builder)
}
