package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.InviteWithMetadataData
import dev.kord.core.entity.InviteWithMetadata
import dev.kord.core.entity.channel.CategorizableChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.InviteCreateBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface CategorizableChannelBehavior : TopGuildChannelBehavior {

    /**
     * Requests to get the invites of this channel.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RestRequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val invites: Flow<InviteWithMetadata>
        get() = flow {
            val responses = kord.rest.channel.getChannelInvites(id)

            for (response in responses) {
                val data = InviteWithMetadataData.from(response)

                emit(InviteWithMetadata(data, kord))
            }
        }

    /**
     * Requests to get this behavior as a [CategorizableChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a guild channel.
     */
    override suspend fun asChannel(): CategorizableChannel = super.asChannel() as CategorizableChannel

    /**
     * Requests to get this behavior as a [CategorizableChannel],
     * returns null if the channel isn't present or if the channel isn't a guild channel.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    override suspend fun asChannelOrNull(): CategorizableChannel? = super.asChannelOrNull() as? CategorizableChannel

    /**
     * Retrieve the [CategorizableChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     */
    override suspend fun fetchChannel(): CategorizableChannel = super.fetchChannel() as CategorizableChannel

    /**
     * Retrieve the [CategorizableChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the channel isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): CategorizableChannel? =
        super.fetchChannelOrNull() as? CategorizableChannel

    /**
     * Returns a new [CategorizableChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): CategorizableChannelBehavior =
        CategorizableChannelBehavior(guildId, id, kord, strategy)
}

internal fun CategorizableChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy,
): CategorizableChannelBehavior = object : CategorizableChannelBehavior {
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

    override fun toString(): String =
        "CategorizableChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
}

/**
 * Request to create an [InviteWithMetadata] for this channel.
 *
 * @return the created [InviteWithMetadata].
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun CategorizableChannelBehavior.createInvite(builder: InviteCreateBuilder.() -> Unit = {}): InviteWithMetadata {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.channel.createInvite(id, builder)
    val data = InviteWithMetadataData.from(response)

    return InviteWithMetadata(data, kord)
}
