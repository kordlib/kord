package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.CategoryBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import java.util.*

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

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when(other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

}