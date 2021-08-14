@file:Suppress("EXPERIMENTAL_API_USAGE")

package dev.kord.voice

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.*
import dev.kord.voice.exception.VoiceConnectionInitializationException
import dev.kord.voice.gateway.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

class VoiceConnectionBuilder(
    val gateway: Gateway,
    val selfId: Snowflake,
    val channelId: Snowflake,
    val guildId: Snowflake
) {
    /**
     * The [CoroutineDispatcher] kord uses to launch suspending tasks. [Dispatchers.Default] by default.
     */
    var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    /**
     * The client used for building [Gateways][Gateway] and [RequestHandlers][RequestHandler]. A default implementation
     * will be used when not set.
     */
    var client: HttpClient? = null

    /**
     * The [AudioProvider] for this [VoiceConnection]. No audio will be sent when one is not set.
     */
    var audioProvider: AudioProvider? = null

    fun audioProvider(audioProvider: AudioProvider) {
        this.audioProvider = audioProvider
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
     * A builder to customize the voice connection's underlying [VoiceGateway].
     */
    fun voiceGateway(builder: DefaultVoiceGatewayBuilder.() -> Unit) {
        this.voiceGatewayBuilder = builder
    }

    private suspend fun Gateway.updateVoiceState(): Pair<VoiceConnectionData, VoiceGatewayConfiguration> {
        gateway.send(
            UpdateVoiceStatus(
                guildId = guildId,
                channelId = channelId,
                selfMute = selfMute,
                selfDeaf = selfDeaf,
            )
        )

        return withTimeoutOrNull(2000) {
            val voiceStateUpdate = gateway.events.filterIsInstance<VoiceStateUpdate>().first().voiceState
            val voiceServerUpdate = gateway.events.filterIsInstance<VoiceServerUpdate>().first().voiceServerUpdateData

            VoiceConnectionData(
                selfId = selfId,
                guildId = guildId
            ) to VoiceGatewayConfiguration(
                channelId = channelId,
                token = voiceServerUpdate.token,
                sessionId = voiceStateUpdate.sessionId,
                endpoint = "wss://${voiceServerUpdate.endpoint}?v=4"
            )
        } ?: throw VoiceConnectionInitializationException("Did not receive a VoiceStateUpdate and VoiceServerUpdate in time!")
    }

    /**
     * @throws dev.kord.voice.exception.VoiceConnectionInitializationException when there was a problem retrieving voice information from Discord.
     */
    suspend fun build(): VoiceConnection {
        val (voiceConnectionData, initialGatewayConfiguration) = gateway.updateVoiceState()

        val voiceGateway = DefaultVoiceGatewayBuilder(selfId, guildId)
            .also { voiceGatewayBuilder?.invoke(it) }
            .build()

        return VoiceConnection(
            gateway,
            voiceGateway,
            initialGatewayConfiguration,
            voiceConnectionData,
            audioProvider ?: AudioProvider { null },
            frameInterceptorFactory ?: { DefaultFrameInterceptor(it) },
            defaultDispatcher
        )
    }
}
