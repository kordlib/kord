package dev.kord.core.entity

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.unwrap
import dev.kord.common.entity.optional.value
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildScheduledEventBehavior
import dev.kord.core.cache.data.GuildScheduledEventData
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.datetime.Instant

/**
 * An instance of a
 * [Guild scheduled event](https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event)
 * belonging to a specific guild.
 */
public class GuildScheduledEvent(
    public val data: GuildScheduledEventData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : GuildScheduledEventBehavior {

    /**
     * The id of this event.
     */
    public override val id: Snowflake
        get() = data.id

    /**
     * The id of the guild this event belongs to.
     */
    public override val guildId: Snowflake
        get() = data.guildId

    /**
     * The id of the channel this event will be hosted in, or `null` if [entityType] is
     * [External][ScheduledEntityType.External].
     */
    public val channelId: Snowflake?
        get() = data.channelId

    /**
     * The id of the user that created this event.
     *
     * This is only available for events created after 2021-10-25.
     */
    public val creatorId: Snowflake?
        get() = data.creatorId.value

    /**
     * The name of this event.
     */
    public val name: String
        get() = data.name

    /**
     * The description of this event, if any.
     */
    public val description: String?
        get() = data.description.value

    /**
     * The [Instant] in which this event will start.
     */
    public val scheduledStartTime: Instant
        get() = data.scheduledStartTime

    /**
     * The [Instant] in which this event will end, if any.
     */
    public val scheduledEndTime: Instant?
        get() = data.scheduledEndTime

    /**
     * The [privacy level][GuildScheduledEventPrivacyLevel] of this event.
     */
    public val privacyLevel: GuildScheduledEventPrivacyLevel
        get() = data.privacyLevel

    /**
     * The [status][GuildScheduledEventStatus] of this event.
     */
    public val status: GuildScheduledEventStatus
        get() = data.status

    /** The id of an entity associated with this event. */
    public val entityId: Snowflake?
        get() = data.entityId

    /**
     * The [type][ScheduledEntityType] of this event.
     */
    public val entityType: ScheduledEntityType
        get() = data.entityType

    /**
     * Additional [metadata][GuildScheduledEventEntityMetadata] for this event.
     */
    public val entityMetadata: GuildScheduledEventEntityMetadata?
        get() = data.entityMetadata

    /** The [user][User] that created this event. */
    public val creator: User?
        get() = data.creator.unwrap { User(it, kord, supplier) }

    /**
     * The number of users subscribed to this event.
     */
    public val userCount: Int?
        get() = data.userCount.value

    /** The cover image hash of this event. */
    public val imageHash: String? get() = data.image.value

    /**
     * Requests the [Guild] this event belongs to.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests the [Guild] this event belongs to,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests the [TopGuildChannel] this event will be hosted in,
     * returns null if the [TopGuildChannel] isn't present or [entityType] is [External][ScheduledEntityType.External].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): TopGuildChannel? = data.channelId?.let { supplier.getChannelOfOrNull(it) }

    /**
     * Requests the channel this event will be hosted in, if it is of type [T],
     * returns `null` if [entityType] is [External][ScheduledEntityType.External], the channel is not present or not of
     * type [T].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend inline fun <reified T : TopGuildChannel> getChannelOfOrNull(): T? =
        data.channelId?.let { supplier.getChannelOfOrNull(it) }

    override suspend fun asGuildScheduledEvent(): GuildScheduledEvent = this
    override suspend fun asGuildScheduledEventOrNull(): GuildScheduledEvent = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable = GuildScheduledEvent(
        data, kord, strategy.supply(kord)
    )
}
