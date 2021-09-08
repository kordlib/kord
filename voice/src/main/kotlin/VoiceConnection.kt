package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Gateway
import dev.kord.gateway.UpdateVoiceStatus
import dev.kord.voice.gateway.*
import dev.kord.voice.handlers.StreamsHandler
import dev.kord.voice.handlers.UdpLifeCycleHandler
import dev.kord.voice.handlers.VoiceUpdateEventHandler
import dev.kord.voice.streams.Streams
import dev.kord.voice.udp.*
import dev.kord.voice.udp.AudioFrameSender
import kotlinx.coroutines.*
import mu.KotlinLogging
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext

/**
 * Data that represents a [VoiceConnection], these will never change during the life time of a [VoiceConnection].
 *
 * @param selfId the id of the bot connecting to a voice channel.
 * @param guildId the id of the guild that the bot is connecting to.
 * @param sessionId the id of the current voice session, given by Discord.
 */
data class VoiceConnectionData(
    val selfId: Snowflake,
    val guildId: Snowflake,
    val sessionId: String
)

private val voiceConnectionLogger = KotlinLogging.logger { }

/**
 * A [VoiceConnection] is an adapter that forms the concept of a voice connection, or a connection between you and Discord voice servers.
 *
 * @param gateway the [Gateway] that handles events for the guild this [VoiceConnection] represents.
 * @param voiceGateway the underlying [VoiceGateway] for this voice connection.
 * @param data the data representing this [VoiceConnection].
 * @param voiceGatewayConfiguration the configuration used on each new [connect], for the [voiceGateway].
 * @param audioProvider a [AudioProvider] that will provide [AudioFrame] when required.
 * @param frameInterceptorFactory a factory for [FrameInterceptor]s that is used whenever audio is ready to be sent. See [FrameInterceptor] and [DefaultFrameInterceptor].
 * @param voiceDispatcher the dispatcher used for this voice connection.
 */
@KordVoice
class VoiceConnection(
    val data: VoiceConnectionData,
    val gateway: Gateway,
    val voiceGateway: VoiceGateway,
    val udp: VoiceUdpConnection,
    var voiceGatewayConfiguration: VoiceGatewayConfiguration,
    val streams: Streams,
    val audioProvider: AudioProvider,
    val frameSender: AudioFrameSender,
    val frameInterceptorFactory: (FrameInterceptorContext) -> FrameInterceptor,
    voiceDispatcher: CoroutineDispatcher
) : CoroutineScope {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + voiceDispatcher + CoroutineName("Voice Connection for Guild ${data.guildId.value}")

    init {
        // handle voice state/server updates (e.g., a move, disconnect, voice server change, etc.)
        VoiceUpdateEventHandler(gateway.events, this)

        // keep Streams up to date.
        StreamsHandler(voiceGateway.events, streams)

        // handle the lifecycle of the udp connection
        UdpLifeCycleHandler(voiceGateway.events, this)
    }

    /**
     * Logs into the voice gateway, and begins the process of an audio-ready voice session.
     */
    fun connect() = launch {
        voiceGateway.start(voiceGatewayConfiguration)
    }

    /**
     * Disconnects from the voice gateway, does not change the voice state.
     */
    suspend fun disconnect() {
        voiceGateway.stop()
    }

    /**
     * Disconnects from Discord voice servers, and leaves the voice channel.
     */
    suspend fun leave() {
        disconnect()

        gateway.send(
            UpdateVoiceStatus(
                guildId = data.guildId,
                channelId = null,
                selfMute = false,
                selfDeaf = false
            )
        )
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
@OptIn(ExperimentalContracts::class)
suspend inline fun VoiceConnection(
    gateway: Gateway,
    selfId: Snowflake,
    channelId: Snowflake,
    guildId: Snowflake,
    builder: VoiceConnectionBuilder.() -> Unit = {}
): VoiceConnection {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return VoiceConnectionBuilder(gateway, selfId, channelId, guildId).apply(builder).build()
}