package dev.kord.core.behavior.channel

import dev.kord.cache.api.query
import dev.kord.common.annotation.KordVoice
import dev.kord.common.exception.RequestException
import dev.kord.core.cache.data.VoiceStateData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.VoiceState
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.exception.GatewayNotFoundException
import dev.kord.voice.VoiceConnection
import dev.kord.voice.VoiceConnectionBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public interface BaseVoiceChannelBehavior : TopGuildChannelBehavior {

    /**
     * Requests to retrieve the present voice states of this channel.
     *
     * This property is not resolvable through REST and will always use [KordCache] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val voiceStates: Flow<VoiceState>
        get() = kord.cache.query<VoiceStateData> { idEq(VoiceStateData::channelId, id) }
            .asFlow()
            .map { VoiceState(it, kord) }

    /**
     * Connect to this [VoiceChannel] and create a [VoiceConnection] for this voice session.
     *
     * @param builder a builder for the [VoiceConnection].
     * @throws GatewayNotFoundException when there is no associated [dev.kord.gateway.Gateway] for the [dev.kord.core.entity.Guild] this channel is in.
     * @throws dev.kord.voice.exception.VoiceConnectionInitializationException when there was a problem retrieving voice information from Discord.
     * @return a [VoiceConnection] representing the connection to this [VoiceConnection].
     */
    @KordVoice
    suspend fun connect(builder: VoiceConnectionBuilder.() -> Unit): VoiceConnection {
        val voiceConnection = VoiceConnection(
            getGuild().gateway ?: GatewayNotFoundException.voiceConnectionGatewayNotFound(guildId),
            kord.selfId,
            id,
            guildId,
            builder
        )

        voiceConnection.connect()

        return voiceConnection
    }
}
