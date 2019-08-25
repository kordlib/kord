package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateVoiceChannelBuilder
import com.gitlab.kordlib.core.`object`.data.ChannelData
import com.gitlab.kordlib.core.`object`.data.VoiceStateData
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.VoiceState
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The behavior of a Discord Voice Channel associated to a guild.
 */
interface VoiceChannelBehavior : GuildChannelBehavior {

    /**
     * Requests to retrieve the voice states of this channel, if cached.
     */
    val voiceStates: Flow<VoiceState> get() =
            kord.cache.find<VoiceStateData> { VoiceStateData::channelId eq id.value }
                    .asFlow()
                    .map { VoiceState(it, kord) }

    override suspend fun asChannel(): VoiceChannel {
        return super.asChannel() as VoiceChannel
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord) = object : VoiceChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

/**
 * Requests to edit this channel.
 *
 * @return The edited [VoiceChannel].
 */
@Suppress("NAME_SHADOWING")
suspend inline fun VoiceChannelBehavior.edit(builder: UpdateVoiceChannelBuilder.() -> Unit): VoiceChannel {
    val builder = UpdateVoiceChannelBuilder().apply(builder)
    val reason = builder.reason

    val request = builder.toRequest()
    val response = kord.rest.channel.patchChannel(id.value, request, reason)

    val data = ChannelData.from(response)
    return Channel.from(data, kord) as VoiceChannel
}