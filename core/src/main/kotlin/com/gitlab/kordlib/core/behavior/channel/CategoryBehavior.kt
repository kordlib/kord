package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.annotation.DeprecatedSinceKord
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.rest.builder.channel.CategoryModifyBuilder
import com.gitlab.kordlib.rest.builder.channel.NewsChannelCreateBuilder
import com.gitlab.kordlib.rest.builder.channel.TextChannelCreateBuilder
import com.gitlab.kordlib.rest.builder.channel.VoiceChannelCreateBuilder
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.service.createNewsChannel
import com.gitlab.kordlib.rest.service.createTextChannel
import com.gitlab.kordlib.rest.service.createVoiceChannel
import com.gitlab.kordlib.rest.service.patchCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a Discord category associated to a [guild].
 */
interface CategoryBehavior : GuildChannelBehavior {

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
    override suspend fun asChannelOrNull(): Category? = supplier.getChannelOf(id)


    /**
     * Requests to get the channels that belong to this [Category].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val channels: Flow<CategorizableChannel>
        get() = supplier.getGuildChannels(guildId)
                .filterIsInstance<CategorizableChannel>()
                .filter { it.categoryId == id }


    /**
     * Returns a new [CategoryBehavior] with the given [strategy].
     */
    override fun withStrategy(
            strategy: EntitySupplyStrategy<*>,
    ): CategoryBehavior = CategoryBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(
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
                is GuildChannelBehavior -> other.id == id && other.guildId == guildId
                is ChannelBehavior -> other.id == id
                else -> false
            }

            override fun toString(): String {
                return "CategoryBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
            }

        }
    }
}

/**
 * Requests to edit this category.
 *
 * @return The edited [Category].
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend fun CategoryBehavior.edit(builder: CategoryModifyBuilder.() -> Unit): Category {
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

@OptIn(ExperimentalContracts::class)
suspend inline fun CategoryBehavior.createTextChannel(name: String, builder: TextChannelCreateBuilder.() -> Unit = {}): TextChannel {
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
@OptIn(ExperimentalContracts::class)
suspend inline fun CategoryBehavior.createVoiceChannel(name: String, builder: VoiceChannelCreateBuilder.() -> Unit = {}): VoiceChannel {
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
@OptIn(ExperimentalContracts::class)
suspend inline fun CategoryBehavior.createNewsChannel(name: String, builder: NewsChannelCreateBuilder.() -> Unit = {}): NewsChannel {
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
