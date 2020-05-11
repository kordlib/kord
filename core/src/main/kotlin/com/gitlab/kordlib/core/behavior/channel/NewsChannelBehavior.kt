package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.rest.builder.channel.NewsChannelModifyBuilder
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.NewsChannel
import com.gitlab.kordlib.rest.service.patchNewsChannel
import java.util.*

/**
 * The behavior of a Discord News Channel associated to a guild.
 */
interface NewsChannelBehavior : GuildMessageChannelBehavior {

    override suspend fun asChannel(): NewsChannel {
        return super.asChannel() as NewsChannel
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord): NewsChannelBehavior = object : NewsChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord

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
 * Requests to edit this channel.
 *
 * @return The edited [NewsChannel].
 */
suspend inline fun NewsChannelBehavior.edit(builder: NewsChannelModifyBuilder.() -> Unit): NewsChannel {
    val response = kord.rest.channel.patchNewsChannel(id.value, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as NewsChannel
}