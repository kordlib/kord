package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.rest.builder.channel.VoiceChannelModifyBuilder
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.cache.data.VoiceStateData
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.VoiceState
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import com.gitlab.kordlib.rest.service.patchVoiceChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * The behavior of a Discord Voice Channel associated to a guild.
 */
interface VoiceChannelBehavior : GuildChannelBehavior {

    /**
     * Requests to retrieve the voice states of this channel, if cached.
     */
    val voiceStates: Flow<VoiceState> get() =
            kord.cache.find<VoiceStateData> { VoiceStateData::channelId eq id.longValue }
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
 * @return The edited [VoiceChannel].
 */
suspend inline fun VoiceChannelBehavior.edit(builder: VoiceChannelModifyBuilder.() -> Unit): VoiceChannel {
    val response = kord.rest.channel.patchVoiceChannel(id.value, builder)

    val data = ChannelData.from(response)
    return Channel.from(data, kord) as VoiceChannel
}