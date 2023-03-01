@file:JvmName("BaseVoiceChannelBehaviorJvm")

package dev.kord.core.behavior.channel

import dev.kord.common.annotation.KordVoice
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.exception.GatewayNotFoundException
import dev.kord.voice.VoiceConnection
import dev.kord.voice.VoiceConnectionBuilder

/**
 * Connect to this [VoiceChannel] and create a [VoiceConnection] for this voice session.
 *
 * @param builder a builder for the [VoiceConnection].
 * @throws GatewayNotFoundException when there is no associated [dev.kord.gateway.Gateway] for the [dev.kord.core.entity.Guild] this channel is in.
 * @throws dev.kord.voice.exception.VoiceConnectionInitializationException when there was a problem retrieving voice information from Discord.
 * @return a [VoiceConnection] representing the connection to this [VoiceConnection].
 */
@KordVoice
public suspend fun BaseVoiceChannelBehavior.connect(builder: VoiceConnectionBuilder.() -> Unit): VoiceConnection {
    val voiceConnection = VoiceConnection(
        guild.gateway ?: GatewayNotFoundException.voiceConnectionGatewayNotFound(guildId),
        kord.selfId,
        id,
        guildId,
        builder
    )

    voiceConnection.connect()

    return voiceConnection
}
