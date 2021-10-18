@file:Suppress("EXPERIMENTAL_API_USAGE")

package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Gateway
import dev.kord.gateway.UpdateVoiceStatus
import dev.kord.gateway.VoiceServerUpdate
import dev.kord.gateway.VoiceStateUpdate
import dev.kord.voice.exception.VoiceConnectionInitializationException
import dev.kord.voice.gateway.DefaultVoiceGatewayBuilder
import dev.kord.voice.gateway.VoiceGateway
import dev.kord.voice.gateway.VoiceGatewayConfiguration
import dev.kord.voice.streams.DefaultStreams
import dev.kord.voice.streams.NOPStreams
import dev.kord.voice.streams.Streams
import dev.kord.voice.udp.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

@KordVoice
class VoiceConnectionBuilder(
    var gateway: Gateway,
    var selfId: Snowflake,
    var channelId: Snowflake,
    var guildId: Snowflake
) {
    /**
     * The amount in milliseconds to wait for the events required to create a [VoiceConnection]. Default is 5000, or 5 seconds.
     */
    var timeout: Long = 5000

    /**
     * The [AudioProvider] for this [VoiceConnection]. No audio will be provided when one is not set.
     */
    var audioProvider: AudioProvider? = null

    /**
     * The [dev.kord.voice.udp.AudioFrameSender] for this [VoiceConnection]. If null, [dev.kord.voice.udp.DefaultAudioFrameSender]
     * will be used.
     */
    var audioSender: AudioFrameSender? = null

    fun audioProvider(provider: AudioProvider) {
        this.audioProvider = provider
    }

    /**
     * The [FrameInterceptor] factory for this [VoiceConnection].
     * When one is not set, a factory will be used to create the default interceptor, see [DefaultFrameInterceptor].
     * This factory will be used to create a new [FrameInterceptor] whenever audio is ready to be sent.
     */
    var frameInterceptorFactory: ((FrameInterceptorContext) -> FrameInterceptor)? = null

    fun frameInterceptor(factory: (FrameInterceptorContext) -> FrameInterceptor) {
        this.frameInterceptorFactory = factory
    }

    /**
     * A boolean indicating whether your voice state will be muted.
     */
    var selfMute: Boolean = false

    /**
     * A boolean indicating whether your voice state will be deafened.
     */
    var selfDeaf: Boolean = false

    private var voiceGatewayBuilder: (DefaultVoiceGatewayBuilder.() -> Unit)? = null

    /**
     * A [dev.kord.voice.udp.VoiceUdpSocket] implementation to be used. If null, a default will be used.
     */
    var udpSocket: VoiceUdpSocket? = null

    /**
     * A flag to control the implementation of [streams]. Set to false by default.
     * When set to false, a NOP implementation will be used.
     * When set to true, a proper receiving implementation will be used.
     */
    var receiveVoice: Boolean = false

    /**
     * A [Streams] implementation to be used. This will override the [receiveVoice] flag.
     */
    var streams: Streams? = null

    /**
     * A builder to customize the voice connection's underlying [VoiceGateway].
     */
    fun voiceGateway(builder: DefaultVoiceGatewayBuilder.() -> Unit) {
        this.voiceGatewayBuilder = builder
    }

    private suspend fun Gateway.updateVoiceState(): Pair<VoiceConnectionData, VoiceGatewayConfiguration> = coroutineScope {
        val voiceStateDeferred = async {
            withTimeoutOrNull(timeout) {
                gateway.events.filterIsInstance<VoiceStateUpdate>()
                    .filter { it.voiceState.guildId.value == guildId && it.voiceState.userId == selfId }
                    .first()
                    .voiceState
            }
        }

        val voiceServerDeferred = async {
            withTimeoutOrNull(timeout) {
                gateway.events.filterIsInstance<VoiceServerUpdate>()
                    .filter { it.voiceServerUpdateData.guildId == guildId }
                    .first()
                    .voiceServerUpdateData
            }
        }

        send(
            UpdateVoiceStatus(
                guildId = guildId,
                channelId = channelId,
                selfMute = selfMute,
                selfDeaf = selfDeaf,
            )
        )

        val voiceServer = voiceServerDeferred.await()
        val voiceState = voiceStateDeferred.await()

        if (voiceServer == null || voiceState == null)
            throw VoiceConnectionInitializationException("Did not receive a VoiceStateUpdate and or a VoiceServerUpdate in time!")

        VoiceConnectionData(
            selfId,
            guildId,
            voiceState.sessionId
        ) to VoiceGatewayConfiguration(
            voiceServer.token,
            "wss://${voiceServer.endpoint}?v=4"
        )
    }

    /**
     * @throws dev.kord.voice.exception.VoiceConnectionInitializationException when there was a problem retrieving voice information from Discord.
     */
    suspend fun build(): VoiceConnection {
        val (voiceConnectionData, initialGatewayConfiguration) = gateway.updateVoiceState()

        val voiceGateway = DefaultVoiceGatewayBuilder(selfId, guildId, voiceConnectionData.sessionId)
            .also { voiceGatewayBuilder?.invoke(it) }
            .build()
        val udpSocket = udpSocket ?: GlobalVoiceUdpSocket
        val audioProvider = audioProvider ?: EmptyAudioPlayerProvider
        val audioSender =
            audioSender ?: DefaultAudioFrameSender(DefaultAudioFrameSenderData(udpSocket))
        val frameInterceptorFactory = frameInterceptorFactory ?: { DefaultFrameInterceptor(it) }
        val streams =
            streams ?: if (receiveVoice) DefaultStreams(voiceGateway, udpSocket) else NOPStreams

        return VoiceConnection(
            voiceConnectionData,
            gateway,
            voiceGateway,
            udpSocket,
            initialGatewayConfiguration,
            streams,
            audioProvider,
            audioSender,
            frameInterceptorFactory,
        )
    }

    // we can't use the SAM feature or else we break the IR backend, so lets just use this object instead
    private object EmptyAudioPlayerProvider : AudioProvider {
        override suspend fun provide(): AudioFrame? = null
    }
}
