package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Gateway
import dev.kord.gateway.UpdateVoiceStatus
import dev.kord.gateway.VoiceServerUpdate
import dev.kord.gateway.VoiceStateUpdate
import dev.kord.voice.encryption.strategies.LiteNonceStrategy
import dev.kord.voice.encryption.strategies.NonceStrategy
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
public class VoiceConnectionBuilder(
    public var gateway: Gateway,
    public var selfId: Snowflake,
    public var channelId: Snowflake,
    public var guildId: Snowflake
) {
    /**
     * The amount in milliseconds to wait for the events required to create a [VoiceConnection]. Default is 5000, or 5 seconds.
     */
    public var timeout: Long = 5000

    /**
     * The [AudioProvider] for this [VoiceConnection]. No audio will be provided when one is not set.
     */
    public var audioProvider: AudioProvider? = null

    public fun audioProvider(provider: AudioProvider) {
        this.audioProvider = provider
    }

    /**
     * The [FrameInterceptor] for this [VoiceConnection].
     * If `null`, [DefaultFrameInterceptor] will be used.
     */
    public var frameInterceptor: FrameInterceptor? = null

    public fun frameInterceptor(frameInterceptor: FrameInterceptor) {
        this.frameInterceptor = frameInterceptor
    }

    /**
     * The [dev.kord.voice.udp.AudioFrameSender] for this [VoiceConnection]. If null, [dev.kord.voice.udp.DefaultAudioFrameSender]
     * will be used.
     */
    public var audioSender: AudioFrameSender? = null

    /**
     * The nonce strategy to be used for the encryption of audio packets.
     * If `null`, [dev.kord.voice.encryption.strategies.LiteNonceStrategy] will be used.
     */
    public var nonceStrategy: NonceStrategy? = null

    /**
     * A boolean indicating whether your voice state will be muted.
     */
    public var selfMute: Boolean = false

    /**
     * A boolean indicating whether your voice state will be deafened.
     */
    public var selfDeaf: Boolean = false

    private var voiceGatewayBuilder: (DefaultVoiceGatewayBuilder.() -> Unit)? = null

    /**
     * A [dev.kord.voice.udp.VoiceUdpSocket] implementation to be used. If null, a default will be used.
     */
    public var udpSocket: VoiceUdpSocket? = null

    /**
     * A flag to control the implementation of [streams]. Set to false by default.
     * When set to false, a NOP implementation will be used.
     * When set to true, a proper receiving implementation will be used.
     */
    public var receiveVoice: Boolean = false

    /**
     * A [Streams] implementation to be used. This will override the [receiveVoice] flag.
     */
    public var streams: Streams? = null

    /**
     * A builder to customize the voice connection's underlying [VoiceGateway].
     */
    public fun voiceGateway(builder: DefaultVoiceGatewayBuilder.() -> Unit) {
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
    public suspend fun build(): VoiceConnection {
        val (voiceConnectionData, initialGatewayConfiguration) = gateway.updateVoiceState()

        val voiceGateway = DefaultVoiceGatewayBuilder(selfId, guildId, voiceConnectionData.sessionId)
            .also { voiceGatewayBuilder?.invoke(it) }
            .build()
        val udpSocket = udpSocket ?: GlobalVoiceUdpSocket
        val audioProvider = audioProvider ?: EmptyAudioPlayerProvider
        val nonceStrategy = nonceStrategy ?: LiteNonceStrategy()
        val frameInterceptor = frameInterceptor ?: DefaultFrameInterceptor()
        val audioSender =
            audioSender ?: DefaultAudioFrameSender(
                DefaultAudioFrameSenderData(
                    udpSocket,
                    frameInterceptor,
                    audioProvider,
                    nonceStrategy
                )
            )
        val streams =
            streams ?: if (receiveVoice) DefaultStreams(voiceGateway, udpSocket, nonceStrategy) else NOPStreams

        return VoiceConnection(
            voiceConnectionData,
            gateway,
            voiceGateway,
            udpSocket,
            initialGatewayConfiguration,
            streams,
            audioProvider,
            frameInterceptor,
            audioSender,
            nonceStrategy
        )
    }

    // we can't use the SAM feature or else we break the IR backend, so lets just use this object instead
    private object EmptyAudioPlayerProvider : AudioProvider {
        override suspend fun provide(): AudioFrame? = null
    }
}
