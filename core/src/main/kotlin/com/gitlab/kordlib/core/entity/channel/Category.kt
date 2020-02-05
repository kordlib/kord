package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.CategoryBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.common.entity.Snowflake

/**
 * An instance of a Discord category associated to a [guild].
 */
data class Category(override val data: ChannelData, override val kord: Kord) : GuildChannel, CategoryBehavior {

    override val id: Snowflake
        get() = super.id

    override val guildId: Snowflake
        get() = super.guildId

    override val guild get() = super<GuildChannel>.guild

    override suspend fun asChannel(): Category = this

    override fun compareTo(other: Entity): Int {
        return super<GuildChannel>.compareTo(other)
    }

}