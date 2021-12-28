package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.json.request.BulkDeleteRequest
import dev.kord.rest.request.RestRequestException
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

/**
 * The behavior of a Discord message channel associated to a [guild].
 */
public interface GuildMessageChannelBehavior : GuildChannelBehavior, MessageChannelBehavior {


    /**
     * Requests to bulk delete the [messages].
     * Messages older than 14 days will be deleted individually.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun bulkDelete(messages: Iterable<Snowflake>, reason: String? = null) {
        val daysLimit = Clock.System.now() - 14.days
        //split up in bulk delete and manual delete
        // if message.timeMark + 14 days > now, then the message isn't 14 days old yet, and we can add it to the bulk delete
        // if message.timeMark + 14 days < now, then the message is more than 14 days old, and we'll have to manually delete them
        val (younger, older) = messages.partition { it.timestamp > daysLimit }

        younger.chunked(100).forEach {
            if (it.size < 2) kord.rest.channel.deleteMessage(id, it.first(), reason)
            else kord.rest.channel.bulkDelete(id, BulkDeleteRequest(it), reason)
        }

        older.forEach { kord.rest.channel.deleteMessage(id, it, reason) }
    }

    override suspend fun asChannel(): GuildMessageChannel {
        return super<GuildChannelBehavior>.asChannel() as GuildMessageChannel
    }

    override suspend fun asChannelOrNull(): GuildMessageChannel? {
        return super<GuildChannelBehavior>.asChannelOrNull() as? GuildMessageChannel
    }


    /**
     * Retrieve the [GuildMessageChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): GuildMessageChannel =
        super<GuildChannelBehavior>.fetchChannel() as GuildMessageChannel


    /**
     * Retrieve the [GuildMessageChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [GuildMessageChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): GuildMessageChannel? =
        super<GuildChannelBehavior>.fetchChannelOrNull() as? GuildMessageChannel

    /**
     * Returns a new [GuildMessageChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildMessageChannelBehavior =
        GuildMessageChannelBehavior(guildId, id, kord, strategy)
}

internal fun GuildMessageChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
) = object : GuildMessageChannelBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "GuildMessageChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}
