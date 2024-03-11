package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Gateway
import dev.kord.gateway.UpdateVoiceStatus
import dev.kord.voice.encryption.strategies.NonceStrategy
import dev.kord.voice.gateway.VoiceGateway
import dev.kord.voice.gateway.VoiceGatewayConfiguration
import dev.kord.voice.handlers.StreamsHandler
import dev.kord.voice.handlers.UdpLifeCycleHandler
import dev.kord.voice.handlers.VoiceUpdateEventHandler
import dev.kord.voice.streams.Streams
import dev.kord.voice.udp.AudioFrameSender
import dev.kord.voice.udp.VoiceUdpSocket
import kotlinx.coroutines.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration

/**
 * Data that represents a [VoiceConnection], these will never change during the lifetime of a [VoiceConnection].
 *
 * @param selfId the id of the bot connecting to a voice channel.
 * @param guildId the id of the guild that the bot is connecting to.
 * @param sessionId the id of the current voice session, given by Discord.
 */
public data class VoiceConnectionData(
    val selfId: Snowflake,
    val guildId: Snowflake,
    val sessionId: String,
)

/**
 * A [VoiceConnection] is an adapter that forms the concept of a voice connection, or a connection between you and Discord voice servers.
 *
 * @param gateway the [Gateway] that handles events for the guild this [VoiceConnection] represents.
 * @param voiceGateway the underlying [VoiceGateway] for this voice connection.
 * @param data the data representing this [VoiceConnection].
 * @param voiceGatewayConfiguration the configuration used on each new [connect] for the [voiceGateway].
 * @param audioProvider a [AudioProvider] that will provide [AudioFrame] when required.
 * @param frameInterceptor a [FrameInterceptor] that will intercept all outgoing [AudioFrame]s.
 * @param frameSender the [AudioFrameSender] that will handle the sending of audio packets.
 * @param nonceStrategy the [NonceStrategy] that is used during encryption of audio.
 */
@KordVoice
public class VoiceConnection(
    public val data: VoiceConnectionData,
    public val gateway: Gateway,
    public val voiceGateway: VoiceGateway,
    public val socket: VoiceUdpSocket,
    public var voiceGatewayConfiguration: VoiceGatewayConfiguration,
    public val streams: Streams,
    public val audioProvider: AudioProvider,
    public val frameInterceptor: FrameInterceptor,
    public val frameSender: AudioFrameSender,
    public val nonceStrategy: NonceStrategy,
    connectionDetachDuration: Duration
) {
    public val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + CoroutineName("kord-voice-connection[${data.guildId.value}]"))

    init {
        with(scope) {
            launch { VoiceUpdateEventHandler(gateway.events, connectionDetachDuration, this@VoiceConnection).start() }
            launch { StreamsHandler(voiceGateway.events, streams).start() }
            launch { UdpLifeCycleHandler(voiceGateway.events, this@VoiceConnection).start() }
        }
    }

    /**
     * Starts the [VoiceGateway] for this [VoiceConnection].
     * This will begin the process for audio transmission.
     */
    public suspend fun connect(scope: CoroutineScope = this.scope) {
        scope.launch {
            voiceGateway.start(voiceGatewayConfiguration)
        }
    }

    /**
     * Disconnects from the voice servers, does not change the voice state.
     */
    public suspend fun disconnect() {
        voiceGateway.stop()
        socket.stop()
    }

    /**
     * Sends a gateway command to update the connection in the current guild.
     *
     * @param channelId the id of the channel to move to.
     * @param selfMute whether to self mute.
     * @param selfDeaf whether to self deaf.
     */
    public suspend fun move(channelId: Snowflake, selfMute: Boolean = false, selfDeaf: Boolean = false) {
        gateway.send(
            UpdateVoiceStatus(
                guildId = data.guildId,
                channelId, selfMute, selfDeaf
            )
        )
    }

    /**
     * Disconnects from Discord voice servers, and leaves the voice channel.
     */
    public suspend fun leave() {
        gateway.send(
            UpdateVoiceStatus(
                guildId = data.guildId,
                channelId = null,
                selfMute = false,
                selfDeaf = false
            )
        )

        disconnect()
    }

    /**
     * Releases all resources related to this VoiceConnection (except [gateway]) and then stops its CoroutineScope.
     */
    public suspend fun shutdown() {
        leave()
        voiceGateway.detach()

        scope.cancel()
    }
}

/**
 * Builds a [VoiceConnection] configured by the [builder].
 *
 * @param gateway the [Gateway] that handles the guild [guildId].
 * @param selfId the id of yourself.
 * @param channelId the id of the initial voice channel you are connecting to.
 * @param guildId the id of the guild the voice channel resides in.
 * @param builder the builder.
 *
 * @return a [VoiceConnection] that is ready to be used.
 *
 * @throws dev.kord.voice.exception.VoiceConnectionInitializationException when there was a problem retrieving voice information from Discord.
 */
@KordVoice
public suspend inline fun VoiceConnection(
    gateway: Gateway,
    selfId: Snowflake,
    channelId: Snowflake,
    guildId: Snowflake,
    builder: VoiceConnectionBuilder.() -> Unit = {}
): VoiceConnection {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return VoiceConnectionBuilder(gateway, selfId, channelId, guildId).apply(builder).build()
}
