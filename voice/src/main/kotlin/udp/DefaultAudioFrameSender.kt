package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.*
import dev.kord.voice.encryption.strategies.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

private val audioFrameSenderLogger = KotlinLogging.logger { }

@KordVoice
public class DefaultAudioFrameSenderData private constructor(private val data: DataHolder) {
    private data class DataHolder(
        val udp: VoiceUdpSocket,
        val interceptor: FrameInterceptor,
        val provider: AudioProvider,
        val nonceStrategy: @Suppress("DEPRECATION") NonceStrategy?,
    )

    @Deprecated(
        "The 'nonceStrategy' property is only used for XSalsa20 Poly1305 encryption. Construct a " +
            "'DefaultAudioFrameSenderData' instance without a 'nonceStrategy' instead. " +
            XSalsa20_CONSTRUCTOR_DEPRECATION,
        ReplaceWith(
            "DefaultAudioFrameSenderData(udp, interceptor, provider)",
            imports = ["dev.kord.voice.udp.DefaultAudioFrameSenderData"],
        ),
        DeprecationLevel.WARNING,
    )
    public constructor(
        udp: VoiceUdpSocket, interceptor: FrameInterceptor, provider: AudioProvider,
        nonceStrategy: @Suppress("DEPRECATION") NonceStrategy,
    ) : this(udp, interceptor, nonceStrategy, provider)

    internal constructor(
        udp: VoiceUdpSocket, interceptor: FrameInterceptor, nonceStrategy: @Suppress("DEPRECATION") NonceStrategy?,
        provider: AudioProvider,
    ) : this(DataHolder(udp, interceptor, provider, nonceStrategy))

    public constructor(udp: VoiceUdpSocket, interceptor: FrameInterceptor, provider: AudioProvider) :
        this(udp, interceptor, nonceStrategy = null, provider)

    @Deprecated(
        "The 'nonceStrategy' property is only used for XSalsa20 Poly1305 encryption. A 'DefaultAudioFrameSenderData' " +
            "instance can be created without a 'nonceStrategy' in which case this property throws an " +
            "'UnsupportedOperationException'. $XSalsa20_PROPERTY_DEPRECATION",
        level = DeprecationLevel.WARNING,
    )
    public val nonceStrategy: @Suppress("DEPRECATION") NonceStrategy
        get() = data.nonceStrategy ?: throw UnsupportedOperationException(
            "This DefaultAudioFrameSenderData instance was created without a nonceStrategy."
        )

    public val udp: VoiceUdpSocket get() = data.udp
    public val interceptor: FrameInterceptor get() = data.interceptor
    public val provider: AudioProvider get() = data.provider
    internal val strategy get() = data.nonceStrategy

    @Deprecated(
        "The 'nonceStrategy' property is only used for XSalsa20 Poly1305 encryption. A 'DefaultAudioFrameSenderData' " +
            "instance can be created without a 'nonceStrategy' in which case this function throws an " +
            "'UnsupportedOperationException'. $XSalsa20_FUNCTION_DEPRECATION",
        level = DeprecationLevel.WARNING,
    )
    public operator fun component4(): @Suppress("DEPRECATION") NonceStrategy =
        data.nonceStrategy ?: throw UnsupportedOperationException(
            "This DefaultAudioFrameSenderData instance was created without a nonceStrategy."
        )

    public operator fun component1(): VoiceUdpSocket = udp
    public operator fun component2(): FrameInterceptor = interceptor
    public operator fun component3(): AudioProvider = provider

    @Deprecated(
        "The 'nonceStrategy' property is only used for XSalsa20 Poly1305 encryption. Create a copy of this " +
            "'DefaultAudioFrameSenderData' instance without a 'nonceStrategy' instead. $XSalsa20_FUNCTION_DEPRECATION",
        ReplaceWith("this.copy(udp = udp, interceptor = interceptor, provider = provider)"),
        DeprecationLevel.WARNING,
    )
    public fun copy(
        udp: VoiceUdpSocket = this.udp, interceptor: FrameInterceptor = this.interceptor,
        provider: AudioProvider = this.provider,
        nonceStrategy: @Suppress("DEPRECATION") NonceStrategy = NONCE_STRATEGY_SENTINEL,
    ): DefaultAudioFrameSenderData = when {
        nonceStrategy === NONCE_STRATEGY_SENTINEL -> // nonceStrategy not specified, keep old one (might be null)
            DefaultAudioFrameSenderData(udp, interceptor, strategy, provider)
        else -> DefaultAudioFrameSenderData(udp, interceptor, nonceStrategy, provider)
    }

    public fun copy(
        udp: VoiceUdpSocket = this.udp, interceptor: FrameInterceptor = this.interceptor,
        provider: AudioProvider = this.provider,
    ): DefaultAudioFrameSenderData = DefaultAudioFrameSenderData(udp, interceptor, strategy, provider)

    override fun equals(other: Any?): Boolean = other is DefaultAudioFrameSenderData && this.data == other.data
    override fun hashCode(): Int = data.hashCode()
    override fun toString(): String = when (val ns = strategy) {
        null -> "DefaultAudioFrameSenderData(udp=$udp, interceptor=$interceptor, provider=$provider)"
        else -> "DefaultAudioFrameSenderData(udp=$udp, interceptor=$interceptor, provider=$provider, nonceStrategy=$ns)"
    }

    private companion object {
        @Suppress("DEPRECATION") // used as a sentinel value by comparing the identity with ===
        private val NONCE_STRATEGY_SENTINEL: NonceStrategy = SuffixNonceStrategy()
    }
}

@KordVoice
public class DefaultAudioFrameSender(
    public val data: DefaultAudioFrameSenderData
) : AudioFrameSender {
    override suspend fun start(configuration: AudioFrameSenderConfiguration): Unit = coroutineScope {
        var sequence: UShort = Random.nextBits(UShort.SIZE_BITS).toUShort()

        val packetProvider = DefaultAudioPacketProvider(configuration.key, data.strategy, configuration.encryptionMode)

        val frames = Channel<AudioFrame?>(Channel.RENDEZVOUS)
        with(data.provider) { launch { provideFrames(frames) } }

        audioFrameSenderLogger.trace { "audio poller starting." }

        try {
            with(data.interceptor) {
                frames.consumeAsFlow()
                    .intercept(configuration.interceptorConfiguration)
                    .filterNotNull()
                    .map { packetProvider.provide(sequence, sequence * 960u, configuration.ssrc, it.data) }
                    .map { Datagram(ByteReadPacket(it.data, it.dataStart, it.viewSize), configuration.server) }
                    .onEach(data.udp::send)
                    .onEach { sequence++ }
                    .collect()
            }
        } catch (e: Exception) {
            audioFrameSenderLogger.trace(e) { "poller stopped with reason" }
            /* we're done polling, nothing to worry about */
        }
    }
}
