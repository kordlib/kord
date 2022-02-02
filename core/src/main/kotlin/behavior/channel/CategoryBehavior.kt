package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.*
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.rest.builder.channel.CategoryModifyBuilder
import dev.kord.rest.builder.channel.NewsChannelCreateBuilder
import dev.kord.rest.builder.channel.TextChannelCreateBuilder
import dev.kord.rest.builder.channel.VoiceChannelCreateBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.createNewsChannel
import dev.kord.rest.service.createTextChannel
import dev.kord.rest.service.createVoiceChannel
import dev.kord.rest.service.patchCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a Discord category associated to a [guild].
 */
public interface CategoryBehavior : TopGuildChannelBehavior {

    /**
     * Requests to get this behavior as a [Category].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel wasn't a category.
     */
    override suspend fun asChannel(): Category = supplier.getChannelOf(id)

    /**
     * Requests to get this behavior as a [Category],
     * returns null if the channel isn't present or is not a category.
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel wasn't a category.
     */
    override suspend fun asChannelOrNull(): Category? = supplier.getChannelOfOrNull(id)

    /**
     * Retrieve the [Category] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): Category = supplier.getChannelOf(id)


    /**
     * Retrieve the [Category] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [Category] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): Category? = supplier.getChannelOfOrNull(id)


    /**
     * Requests to get the channels that belong to this [Category].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val channels: Flow<CategorizableChannel>
        get() = supplier.getGuildChannels(guildId)
            .filterIsInstance<CategorizableChannel>()
            .filter { it.categoryId == id }


    /**
     * Returns a new [CategoryBehavior] with the given [strategy].
     */
    override fun withStrategy(
        strategy: EntitySupplyStrategy<*>,
    ): CategoryBehavior = CategoryBehavior(guildId, id, kord, strategy)

}


public fun CategoryBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy,
): CategoryBehavior = object : CategoryBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is TopGuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "CategoryBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }

}

/**
 * Requests to edit this category.
 *
 * @return The edited [Category].
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend fun CategoryBehavior.edit(builder: CategoryModifyBuilder.() -> Unit): Category {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.channel.patchCategory(id, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as Category
}

/**
 * Requests to create a new text channel with this category as parent.
 *
 * @return The created [TextChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun CategoryBehavior.createTextChannel(
    name: String,
    builder: TextChannelCreateBuilder.() -> Unit = {}
): TextChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createTextChannel(guildId, name) {
        builder()
        parentId = id
    }
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as TextChannel
}


/**
 * Requests to create a new voice channel with this category as parent.
 *
 * @return The created [VoiceChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun CategoryBehavior.createVoiceChannel(
    name: String,
    builder: VoiceChannelCreateBuilder.() -> Unit = {}
): VoiceChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createVoiceChannel(guildId, name) {
        builder()
        parentId = id
    }
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as VoiceChannel
}

/**
 * Requests to create a new news channel with this category as parent.
 *
 * @return The created [NewsChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun CategoryBehavior.createNewsChannel(
    name: String,
    builder: NewsChannelCreateBuilder.() -> Unit = {}
): NewsChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createNewsChannel(guildId, name) {
        builder()
        parentId = id
    }
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as NewsChannel
}
