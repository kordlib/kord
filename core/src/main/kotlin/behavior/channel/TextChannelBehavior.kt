package dev.kord.core.behavior.channel

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.threads.PrivateThreadParentChannelBehavior
import dev.kord.core.behavior.channel.threads.unsafeStartPublicThreadWithMessage
import dev.kord.core.behavior.channel.threads.unsafeStartThread
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.TextChannelModifyBuilder
import dev.kord.rest.builder.channel.thread.StartThreadBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.patchTextChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.datetime.Instant
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface TextChannelBehavior : PrivateThreadParentChannelBehavior {

    override val activeThreads: Flow<TextChannelThread>
        get() = super.activeThreads.filterIsInstance()

    /**
     * Requests to get this behavior as a [TextChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a [TextChannel].
     */
    override suspend fun asChannel(): TextChannel = super.asChannel() as TextChannel

    /**
     * Requests to get this behavior as a [TextChannel],
     * returns null if the channel isn't present or if the channel isn't a [TextChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun asChannelOrNull(): TextChannel? = super.asChannelOrNull() as? TextChannel

    /**
     * Retrieve the [TextChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): TextChannel = super.fetchChannel() as TextChannel


    /**
     * Retrieve the [TextChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [TextChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): TextChannel? = super.fetchChannelOrNull() as? TextChannel

    public suspend fun startPublicThread(
        name: String,
        archiveDuration: ArchiveDuration = ArchiveDuration.Day,
        builder: StartThreadBuilder.() -> Unit = {}
    ): TextChannelThread {
        return unsafeStartThread(
            name,
            archiveDuration,
            ChannelType.PublicGuildThread,
            builder
        ) as TextChannelThread
    }

    public suspend fun startPrivateThread(
        name: String,
        archiveDuration: ArchiveDuration = ArchiveDuration.Day,
        builder: StartThreadBuilder.() -> Unit = {}
    ): TextChannelThread {
        val startBuilder = StartThreadBuilder(name, archiveDuration, ChannelType.PrivateThread).apply(builder)
        return unsafeStartThread(startBuilder.name, startBuilder.autoArchiveDuration, ChannelType.PrivateThread, builder) as TextChannelThread
    }

    public suspend fun startPublicThreadWithMessage(
        messageId: Snowflake,
        name: String,
        archiveDuration: ArchiveDuration = ArchiveDuration.Day,
        reason: String? = null
    ): TextChannelThread {
        return unsafeStartPublicThreadWithMessage(messageId, name, archiveDuration, reason) as TextChannelThread
    }

    override fun getPublicArchivedThreads(before: Instant?, limit: Int?): Flow<TextChannelThread> {
        return super.getPublicArchivedThreads(before, limit).filterIsInstance()
    }

    override fun getPrivateArchivedThreads(before: Instant?, limit: Int?): Flow<TextChannelThread> {
        return super.getPrivateArchivedThreads(before, limit).filterIsInstance()
    }

    override fun getJoinedPrivateArchivedThreads(before: Snowflake?, limit: Int?): Flow<TextChannelThread> {
        return super.getJoinedPrivateArchivedThreads(before, limit).filterIsInstance()
    }


    /**
     * Returns a new [TextChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TextChannelBehavior =
        TextChannelBehavior(guildId, id, kord, strategy)

}

public fun TextChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): TextChannelBehavior = object : TextChannelBehavior {
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
        return "TextChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}


/**
 * Requests to edit this channel.
 *
 * @return The edited [TextChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun TextChannelBehavior.edit(builder: TextChannelModifyBuilder.() -> Unit): TextChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.channel.patchTextChannel(id, builder)
    val data = ChannelData.from(response)
    return Channel.from(data, kord) as TextChannel
}
