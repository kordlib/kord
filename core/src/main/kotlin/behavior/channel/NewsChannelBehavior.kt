package dev.kord.core.behavior.channel

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.behavior.channel.threads.unsafeStartPublicThreadWithMessage
import dev.kord.core.behavior.channel.threads.unsafeStartThread
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.NewsChannel
import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.NewsChannelModifyBuilder
import dev.kord.rest.json.request.ChannelFollowRequest
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.patchNewsChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.datetime.Instant
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a Discord News Channel associated to a guild.
 */
public interface NewsChannelBehavior : ThreadParentChannelBehavior {

    override val activeThreads: Flow<NewsChannelThread>
        get() = super.activeThreads.filterIsInstance()

    /**
     * Requests to get the this behavior as a [NewsChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a [NewsChannel].
     */
    override suspend fun asChannel(): NewsChannel = super.asChannel() as NewsChannel

    /**
     * Requests to get this behavior as a [NewsChannel],
     * returns null if the channel isn't present or if the channel isn't a news channel.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    override suspend fun asChannelOrNull(): NewsChannel? = super.asChannelOrNull() as? NewsChannel

    /**
     * Retrieve the [NewsChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): NewsChannel = super.fetchChannel() as NewsChannel


    /**
     * Retrieve the [NewsChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [NewsChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): NewsChannel? = super.fetchChannelOrNull() as? NewsChannel


    /**
     * Requests to follow this channel, publishing cross posted messages to the [target] channel.
     *
     * This call requires the bot to have the [Permission.ManageWebhooks] permission.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun follow(target: Snowflake) {
        kord.rest.channel.followNewsChannel(id, ChannelFollowRequest(webhookChannelId = target))
    }


    public suspend fun startPublicThread(
        name: String,
        archiveDuration: ArchiveDuration = ArchiveDuration.Day,
        reason: String? = null
    ): NewsChannelThread {
        return unsafeStartThread(name, archiveDuration, ChannelType.PublicNewsThread) { this.reason = reason } as NewsChannelThread
    }

    public suspend fun startPublicThreadWithMessage(
        messageId: Snowflake,
        name: String,
        archiveDuration: ArchiveDuration = ArchiveDuration.Day,
        reason: String? = null
    ): NewsChannelThread {
        return unsafeStartPublicThreadWithMessage(messageId, name, archiveDuration, reason) as NewsChannelThread
    }


    override fun getPublicArchivedThreads(before: Instant?, limit: Int?): Flow<NewsChannelThread> {
        return super.getPublicArchivedThreads(before, limit).filterIsInstance()
    }

    /**
     * Returns a new [NewsChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): NewsChannelBehavior =
        NewsChannelBehavior(guildId, id, kord, strategy)

}

public fun NewsChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): NewsChannelBehavior = object : NewsChannelBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "NewsChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}

/**
 * Requests to edit this channel.
 *
 * @return The edited [NewsChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun NewsChannelBehavior.edit(builder: NewsChannelModifyBuilder.() -> Unit): NewsChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.channel.patchNewsChannel(id, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as NewsChannel
}
