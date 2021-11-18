package dev.kord.core.entity

import dev.kord.common.entity.GuildScheduledEventEntityMetadata
import dev.kord.common.entity.GuildScheduledEventStatus
import dev.kord.common.entity.ScheduledEntityType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.optional.unwrap
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
 * An instance of a [Guild scheduled event](ADD LINK) belonging to a specific guild.
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
     * The id of the guild this event is on.
     */
    public val guildId: Snowflake
        get() = data.guildId

    /**
     * The id of the channel this event is in, if any.
     */
    public val channelId: Snowflake?
        get() = data.channelId

    /**
     * The id of the user that created the scheduled event
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
     * The [privacy level][StageInstancePrivacyLevel] of this event.
     */
    public val privacyLevel: StageInstancePrivacyLevel
        get() = data.privacyLevel

    /**
     * The [status][GuildScheduledEventStatus] of this event.
     */
    public val status: GuildScheduledEventStatus
        get() = data.status

    public val entityId: Snowflake?
        get() = data.entityId

    /**
     * The [scheduled entity type][ScheduledEntityType] for this event.
     */
    public val entityType: ScheduledEntityType
        get() = data.entityType

    /**
     * The [entity metadata][GuildScheduledEventEntityMetadata] for the scheduled event
     */
    public val entityMetadata: GuildScheduledEventEntityMetadata
        get() = data.entityMetadata

    public val creator: User?
        get() = data.creator.unwrap { User(it, kord, supplier) }

    /**
     * The amount of users subscribed to this event.
     */
    public val userCount: Int
        get() = data.userCount

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
    public suspend fun getChannelOrNull(): TopGuildChannel? = data.channelId?.let { supplier.getChannelOfOrNull(it) }

    /**
     * Requests the channel this event is in, if it is of type [T],
     * returns `null` if the channel is not set, not present or not of type [T]
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
