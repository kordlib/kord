package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.channel.CategorizableChannel
import com.gitlab.kordlib.core.entity.channel.Category
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.rest.builder.channel.CategoryModifyBuilder
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.service.patchCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import java.util.*

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
            strategy: EntitySupplyStrategy<*>
    ): CategoryBehavior = CategoryBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(
                guildId: Snowflake,
                id: Snowflake,
                kord: Kord,
                strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
        ): CategoryBehavior = object : CategoryBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val supplier: EntitySupplier = strategy.supply(kord)

            override fun hashCode(): Int = Objects.hash(id, guildId)

            override fun equals(other: Any?): Boolean = when(other) {
                is GuildChannelBehavior -> other.id == id && other.guildId == guildId
                is ChannelBehavior -> other.id == id
                else -> false
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
@Suppress("NAME_SHADOWING")
suspend fun CategoryBehavior.edit(builder: CategoryModifyBuilder.() -> Unit): Category {
    val response = kord.rest.channel.patchCategory(id.value, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as Category
}