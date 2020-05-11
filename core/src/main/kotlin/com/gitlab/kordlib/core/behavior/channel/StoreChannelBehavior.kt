package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.rest.builder.channel.StoreChannelModifyBuilder
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.StoreChannel
import com.gitlab.kordlib.rest.service.patchStoreChannel
import java.util.*

/**
 * The behavior of a Discord Store Channel associated to a guild.
 */
interface StoreChannelBehavior : GuildChannelBehavior {

    override suspend fun asChannel(): StoreChannel {
        return super.asChannel() as StoreChannel
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord): StoreChannelBehavior = object : StoreChannelBehavior {
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
 * @return The edited [StoreChannel].
 */
suspend inline fun StoreChannelBehavior.edit(builder: StoreChannelModifyBuilder.() -> Unit): StoreChannel {
    val response = kord.rest.channel.patchStoreChannel(id.value, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as StoreChannel
}