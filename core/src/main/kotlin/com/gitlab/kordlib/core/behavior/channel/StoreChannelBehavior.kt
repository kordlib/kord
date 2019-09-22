package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.builder.channel.UpdateStoreChannelBuilder
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.StoreChannel
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
        }
    }

}

/**
 * Requests to edit this channel.
 *
 * @return The edited [StoreChannel].
 */
@Suppress("NAME_SHADOWING")
suspend inline fun StoreChannelBehavior.edit(builder: (UpdateStoreChannelBuilder) -> Unit): StoreChannel {
    val builder = UpdateStoreChannelBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.channel.patchChannel(id.value, request, reason)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as StoreChannel
}