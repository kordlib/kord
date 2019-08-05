package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateCategoryBuilder
import com.gitlab.kordlib.core.behavior.guild.GuildBehavior
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake

/**
 * The behavior of a Discord category associated to a [guild].
 */
interface CategoryBehavior : Entity {
    /**
     * The id of the guild this channel is associated to.
     */
    val guildId: Snowflake

    /**
     * The guild this channel is associated to.
     */
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    companion object {
            internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord) : CategoryBehavior = object : CategoryBehavior {
                override val guildId: Snowflake = guildId
                override val id: Snowflake = id
                override val kord: Kord = kord
            }
    }
}

/**
 * Requests to edit this category.
 *
 * @return The edited [category].
 */
suspend fun CategoryBehavior.edit(builder: UpdateCategoryBuilder.() -> Unit): Nothing /*Category*/ = TODO()