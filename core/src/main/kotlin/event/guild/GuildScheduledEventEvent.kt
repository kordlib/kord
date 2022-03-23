package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.entity.Guild
import dev.kord.core.entity.GuildScheduledEvent
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.coroutines.CoroutineScope

/**
 * Interface of all events related to [GuildScheduledEvents][GuildScheduledEvent].
 *
 * @see GuildScheduledEventCreateEvent
 * @see GuildScheduledEventUpdateEvent
 * @see GuildScheduledEventDeleteEvent
 */
public sealed interface GuildScheduledEventEvent : Event, CoroutineScope, Strategizable {

    /**
     * The [GuildScheduledEvent].
     */
    public val scheduledEvent: GuildScheduledEvent

    /**
     * The id of [scheduledEvent].
     */
    public val scheduledEventId: Snowflake get() = scheduledEvent.id

    /**
     * The id of the guild the event is on.
     */
    public val guildId: Snowflake get() = scheduledEvent.guildId

    /**
     * The id of the channel the event is in, if any.
     */
    public val channelId: Snowflake? get() = scheduledEvent.channelId

    /**
     * Requests the [Guild] this event is on.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests the [Guild] this event is on,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests the [TopGuildChannel] this event is in,
     * returns null if the [TopGuildChannel] isn't present or not set.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): TopGuildChannel? = channelId?.let { supplier.getChannelOfOrNull(it) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildScheduledEventEvent
}
