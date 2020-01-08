package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.rest.builder.channel.TextChannelModifyBuilder
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.TextChannel


interface TextChannelBehavior : GuildMessageChannelBehavior {

    override suspend fun asChannel(): TextChannel {
        return super.asChannel() as TextChannel
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord): TextChannelBehavior = object : TextChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

/**
 * Requests to edit this channel.
 *
 * @return The edited [TextChannel].
 */

@Suppress("NAME_SHADOWING")
suspend inline fun TextChannelBehavior.edit(builder: (TextChannelModifyBuilder) -> Unit): TextChannel {
    val builder = TextChannelModifyBuilder().apply(builder)
    val reason = builder.reason

    val request = builder.toRequest()
    val response = kord.rest.channel.patchChannel(id.value, request, reason)

    val data = ChannelData.from(response)
    return Channel.from(data, kord) as TextChannel
}
