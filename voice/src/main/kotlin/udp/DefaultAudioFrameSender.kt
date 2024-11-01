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
public class DefaultAudioFrameSenderData private constructor(private val wrapper: Wrapper) {
    private data class Wrapper(
        val udp: VoiceUdpSocket,
        val interceptor: FrameInterceptor,
        val provider: AudioProvider,
        val nonceStrategy: @Suppress("DEPRECATION") NonceStrategy?,
    )

    internal val strategy get() = wrapper.nonceStrategy

    public constructor(udp: VoiceUdpSocket, interceptor: FrameInterceptor, provider: AudioProvider) :
        this(Wrapper(udp = udp, interceptor = interceptor, provider = provider, nonceStrategy = null))

    public val udp: VoiceUdpSocket get() = wrapper.udp
    public val interceptor: FrameInterceptor get() = wrapper.interceptor
    public val provider: AudioProvider get() = wrapper.provider
    public operator fun component1(): VoiceUdpSocket = wrapper.udp
    public operator fun component2(): FrameInterceptor = wrapper.interceptor
    public operator fun component3(): AudioProvider = wrapper.provider
    override fun equals(other: Any?): Boolean = other is DefaultAudioFrameSenderData && this.wrapper == other.wrapper
    override fun hashCode(): Int = wrapper.hashCode()
    override fun toString(): String = when (val n = wrapper.nonceStrategy) {
        null -> "DefaultAudioFrameSenderData(udp=${wrapper.udp}, interceptor=${wrapper.interceptor}, " +
            "provider=${wrapper.provider})"
        else -> "DefaultAudioFrameSenderData(udp=${wrapper.udp}, interceptor=${wrapper.interceptor}, " +
            "provider=${wrapper.provider}, nonceStrategy=$n)"
    }

    public fun copy(
        udp: VoiceUdpSocket = wrapper.udp, interceptor: FrameInterceptor = wrapper.interceptor,
        provider: AudioProvider = wrapper.provider,
    ): DefaultAudioFrameSenderData = DefaultAudioFrameSenderData(
        Wrapper(udp = udp, interceptor = interceptor, provider = provider, nonceStrategy = wrapper.nonceStrategy)
    )

    internal constructor(
        udpSocket: VoiceUdpSocket, frameInterceptor: FrameInterceptor,
        strategy: @Suppress("DEPRECATION") NonceStrategy?, audioProvider: AudioProvider,
    ) : this(
        Wrapper(udp = udpSocket, interceptor = frameInterceptor, provider = audioProvider, nonceStrategy = strategy)
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
    ) : this(Wrapper(udp = udp, interceptor = interceptor, provider = provider, nonceStrategy = nonceStrategy))

    @Deprecated(
        "The 'nonceStrategy' property is only used for XSalsa20 Poly1305 encryption. A 'DefaultAudioFrameSenderData' " +
            "instance can be created without a 'nonceStrategy' in which case this property throws an " +
            "'UnsupportedOperationException'. $XSalsa20_PROPERTY_DEPRECATION",
        level = DeprecationLevel.WARNING,
    )
    public val nonceStrategy: @Suppress("DEPRECATION") NonceStrategy
        get() = wrapper.nonceStrategy ?: throw UnsupportedOperationException(
            "This DefaultAudioFrameSenderData instance was created without a nonceStrategy."
        )

    @Deprecated(
        "The 'nonceStrategy' property is only used for XSalsa20 Poly1305 encryption. A 'DefaultAudioFrameSenderData' " +
            "instance can be created without a 'nonceStrategy' in which case this function throws an " +
            "'UnsupportedOperationException'. $XSalsa20_FUNCTION_DEPRECATION",
        level = DeprecationLevel.WARNING,
    )
    public operator fun component4(): @Suppress("DEPRECATION") NonceStrategy =
        wrapper.nonceStrategy ?: throw UnsupportedOperationException(
            "This DefaultAudioFrameSenderData instance was created without a nonceStrategy."
        )

    @Deprecated(
        "The 'nonceStrategy' property is only used for XSalsa20 Poly1305 encryption. Create a copy of this " +
            "'DefaultAudioFrameSenderData' instance without a 'nonceStrategy' instead. $XSalsa20_FUNCTION_DEPRECATION",
        ReplaceWith("this.copy(udp = udp, interceptor = interceptor, provider = provider)"),
        DeprecationLevel.WARNING,
    )
    public fun copy(
        udp: VoiceUdpSocket = wrapper.udp, interceptor: FrameInterceptor = wrapper.interceptor,
        provider: AudioProvider = wrapper.provider,
        nonceStrategy: @Suppress("DEPRECATION") NonceStrategy = NONCE_STRATEGY_SENTINEL,
    ): DefaultAudioFrameSenderData = when {
        // nonceStrategy was not overridden, keep the old one (which might be null)
        nonceStrategy === NONCE_STRATEGY_SENTINEL -> DefaultAudioFrameSenderData(
            Wrapper(udp = udp, interceptor = interceptor, provider = provider, nonceStrategy = wrapper.nonceStrategy)
        )
        else -> DefaultAudioFrameSenderData(
            Wrapper(udp = udp, interceptor = interceptor, provider = provider, nonceStrategy = nonceStrategy)
        )
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

        val packetProvider = DefaultAudioPacketProvider(configuration.key, configuration.encryptionMode, data.strategy)

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
