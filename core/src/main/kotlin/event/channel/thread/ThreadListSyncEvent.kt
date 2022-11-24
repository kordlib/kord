package dev.kord.core.event.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ThreadListSyncData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

/**
 * The event dispatched when a Thread list is synced.
 *
 * See [Thread List Sync](https://discord.com/developers/docs/topics/gateway-events#thread-list-sync)
 *
 * @property data The data for the sync
 */
public class ThreadListSyncEvent(
    public val data: ThreadListSyncData,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {
    /**
     * The ID of the guild that triggered the event.
     */
    public val guildId: Snowflake get() = data.guildId

    /**
     * The [Guild][GuildBehavior] that triggered the event.
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * The parent channel ids whose threads are being synced.
     * If empty, then threads were synced for the entire guild.
     */
    public val channelIds: List<Snowflake> get() = data.channelIds.orEmpty()

    /**
     * The behavior of the channels that contain threads, if the [channelIds] list is not empty.
     */
    public val channelBehaviors: List<ThreadParentChannelBehavior>
        get() = channelIds.map {
            ThreadParentChannelBehavior(guildId, it, kord)
        }

    /**
     * Threads that are being synced for [channelIds].
     *
     * @see [channelIds]
     */
    public val threads: List<ThreadChannel>
        get() = data.threads.mapNotNull {
            Channel.from(it, kord) as? ThreadChannel
        }

    /**
     * [ThreadMember] objects for the current user for each of the synced threads.
     */
    public val members: List<ThreadMember> get() = data.members.map { ThreadMember(it, kord) }

    /**
     * Requests to get the [Guild] for the event
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     * @throws EntityNotFoundException if the guild is `null`.
     */
    public suspend fun getGuild(): Guild {
        return supplier.getGuild(guildId)
    }

    /**
     * Requests to get the [Guild] for the event, returns `null` when the guild isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     */
    public suspend fun getGuildOrNull(): Guild? {
        return supplier.getGuildOrNull(guildId)
    }

    /**
     * Requests to get the channels as a [Flow] of [TopGuildChannel]s for a guild
     */
    public suspend fun getChannels(): Flow<TopGuildChannel> {
        return supplier.getGuildChannels(guildId).filter { it.id in channelIds }
    }

    /**
     * Returns a copy of this class with a new [supplier] provided by the [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadListSyncEvent =
        ThreadListSyncEvent(data, kord, shard, customContext, strategy.supply(kord))
}
