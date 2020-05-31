package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.channel.CategorizableChannel
import com.gitlab.kordlib.core.entity.channel.Category
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.getChannelOf
import com.gitlab.kordlib.rest.builder.channel.CategoryModifyBuilder
import com.gitlab.kordlib.rest.service.patchCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance

/**
 * The behavior of a Discord category associated to a [guild].
 */
interface CategoryBehavior : GuildChannelBehavior {

    /**
     * Requests to get the this behavior as a [Category].
     *
     * Entities will be fetched from the [cache][Kord.cache] firstly and the [RestClient][Kord.rest] secondly.
     */
    override suspend fun asChannel() : Category = strategy.supply(kord).getChannelOf<Category>(id)
    override suspend fun asChannelOrNull() : Category? = strategy.supply(kord).getChannelOf<Category>(id)



    /**
     * Requests to get the channels that belong to this category.
     */
    val channels: Flow<CategorizableChannel> get() = guild.channels.filterIsInstance<CategorizableChannel>().filter { it.categoryId == id }


    /**
     * returns a new [CategoryBehavior] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */

    override fun withStrategy(strategy: EntitySupplyStrategy):CategoryBehavior = CategoryBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy): CategoryBehavior = object : CategoryBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val strategy: EntitySupplyStrategy = strategy
        }
    }
}

/**
 * Requests to edit this category.
 *
 * @return The edited [category].
 */
@Suppress("NAME_SHADOWING")
suspend fun CategoryBehavior.edit(builder: CategoryModifyBuilder.() -> Unit): Category {
    val response = kord.rest.channel.patchCategory(id.value, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as Category
}

