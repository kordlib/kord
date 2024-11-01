package dev.kord.voice.streams

import com.iwebpp.crypto.*
import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.AudioFrame
import dev.kord.voice.EncryptionMode
import dev.kord.voice.XSalsa20_CONSTRUCTOR_DEPRECATION
import dev.kord.voice.XSalsa20_FUNCTION_DEPRECATION
import dev.kord.voice.encryption.*
import dev.kord.voice.encryption.strategies.*
import dev.kord.voice.gateway.Speaking
import dev.kord.voice.gateway.VoiceGateway
import dev.kord.voice.io.*
import dev.kord.voice.udp.DecryptedVoicePacket
import dev.kord.voice.udp.PayloadType
import dev.kord.voice.udp.RTPPacket
import dev.kord.voice.udp.VoiceUdpSocket
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.sockets.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val defaultStreamsLogger = KotlinLogging.logger { }

@KordVoice
public class DefaultStreams internal constructor(
    gateway: VoiceGateway,
    strategy: @Suppress("DEPRECATION") NonceStrategy?,
    udpSocket: VoiceUdpSocket,
) : Streams {
    private val voiceGateway = gateway
    private val udp = udpSocket
    private val nonceStrategy = strategy

    public constructor(voiceGateway: VoiceGateway, udp: VoiceUdpSocket) :
        this(gateway = voiceGateway, strategy = null, udpSocket = udp)

    @Deprecated(
        "The 'nonceStrategy' parameter is only used for XSalsa20 Poly1305 encryption. Construct a 'DefaultStreams' " +
            "instance without a 'NonceStrategy' instead. $XSalsa20_CONSTRUCTOR_DEPRECATION",
        ReplaceWith("DefaultStreams(voiceGateway, udp)", imports = ["dev.kord.voice.streams.DefaultStreams"]),
        DeprecationLevel.WARNING,
    )
    public constructor(
        voiceGateway: VoiceGateway, udp: VoiceUdpSocket, nonceStrategy: @Suppress("DEPRECATION") NonceStrategy,
    ) : this(gateway = voiceGateway, strategy = nonceStrategy, udpSocket = udp)

    internal fun CoroutineScope.listenForIncoming(
        udp: VoiceUdpSocket,
        key: ByteArray,
        server: SocketAddress,
        @Suppress("LocalVariableName") _incomingAudioPackets: MutableSharedFlow<RTPPacket>,
        nonceStrategy: @Suppress("DEPRECATION") NonceStrategy,
        emitVoicePacket: suspend (RTPPacket) -> Unit,
    ) {
        udp.incoming
            .filter { it.address == server }
            .mapNotNull { RTPPacket.fromPacket(it.packet) }
            .filter { it.payloadType == PayloadType.Audio.raw }
            .decrypt(nonceStrategy, key)
            .clean()
            .onEach { _incomingAudioPackets.emit(it) }
            .onEach(emitVoicePacket)
            .launchIn(this)
    }

    private fun CoroutineScope.listenForUserFrames() {
        voiceGateway.events
            .filterIsInstance<Speaking>()
            .buffer(Channel.UNLIMITED)
            .onEach { speaking ->
                _ssrcToUser.update {
                    it.computeIfAbsent(speaking.ssrc) {
                        incomingAudioFrames
                            .filter { (ssrc, _) -> speaking.ssrc == ssrc }
                            .map { (_, frame) -> speaking.userId to frame }
                            .onEach { value -> _incomingUserAudioFrames.emit(value) }
                            .launchIn(this)

                        speaking.userId
                    }

                    it
                }
            }.launchIn(this)
    }

    override suspend fun listen(key: ByteArray, server: SocketAddress, encryptionMode: EncryptionMode) {
        val decryptionDelegate = @Suppress("DEPRECATION") when (encryptionMode) {
            EncryptionMode.AeadAes256GcmRtpSize -> NewDecryptionDelegate(AeadAes256GcmRtpSizeVoicePacketDecryptor(key))
            EncryptionMode.AeadXChaCha20Poly1305RtpSize ->
                NewDecryptionDelegate(AeadXChaCha20Poly1305RtpSizeVoicePacketDecryptor(key))
            EncryptionMode.XSalsa20Poly1305 ->
                LegacyDecryptionDelegate(key, this, nonceStrategy as? NormalNonceStrategy ?: NormalNonceStrategy())
            EncryptionMode.XSalsa20Poly1305Lite ->
                LegacyDecryptionDelegate(key, this, nonceStrategy as? LiteNonceStrategy ?: LiteNonceStrategy())
            EncryptionMode.XSalsa20Poly1305Suffix ->
                LegacyDecryptionDelegate(key, this, nonceStrategy as? SuffixNonceStrategy ?: SuffixNonceStrategy())
            is EncryptionMode.Unknown -> throw UnsupportedOperationException("Unknown encryption mode $encryptionMode")
        }
        listen(decryptionDelegate, server)
    }

    @Deprecated(
        "This functions always uses XSalsa20 Poly1305 encryption. Pass an explicit 'EncryptionMode' instead. A " +
            "'DefaultStreams' instance can be created without a 'NonceStrategy' in which case this function throws " +
            "an 'UnsupportedOperationException'. $XSalsa20_FUNCTION_DEPRECATION",
        ReplaceWith(
            "this.listen(key, server, EncryptionMode.AeadXChaCha20Poly1305RtpSize)",
            imports = ["dev.kord.voice.EncryptionMode"],
        ),
        DeprecationLevel.WARNING,
    )
    override suspend fun listen(key: ByteArray, server: SocketAddress) {
        val strategy = nonceStrategy
            ?: throw UnsupportedOperationException("This DefaultStreams instance was created without a NonceStrategy.")
        listen(LegacyDecryptionDelegate(key, this, strategy), server)
    }

    private suspend fun listen(delegate: DecryptionDelegate, server: SocketAddress): Unit = coroutineScope {
        delegate.listenForIncoming(scope = this, udp, server, _incomingAudioPackets, _incomingVoicePackets)
        listenForUserFrames()
    }

    private val _incomingAudioPackets: MutableSharedFlow<RTPPacket> = MutableSharedFlow()

    override val incomingAudioPackets: SharedFlow<RTPPacket> = _incomingAudioPackets

    private val _incomingVoicePackets = MutableSharedFlow<DecryptedVoicePacket>()
    override val incomingVoicePackets: SharedFlow<DecryptedVoicePacket> get() = _incomingVoicePackets

    override val incomingAudioFrames: Flow<Pair<UInt, AudioFrame>>
        get() = incomingAudioPackets.map { it.ssrc to AudioFrame(it.payload.toByteArray()) }

    private val _incomingUserAudioFrames: MutableSharedFlow<Pair<Snowflake, AudioFrame>> =
        MutableSharedFlow()

    override val incomingUserStreams: SharedFlow<Pair<Snowflake, AudioFrame>> =
        _incomingUserAudioFrames

    private val _ssrcToUser: AtomicRef<MutableMap<UInt, Snowflake>> =
        atomic(mutableMapOf())

    override val ssrcToUser: Map<UInt, Snowflake> get() = _ssrcToUser.value
}

private interface DecryptionDelegate {
    fun listenForIncoming(
        scope: CoroutineScope,
        udp: VoiceUdpSocket,
        server: SocketAddress,
        audioPackets: MutableSharedFlow<RTPPacket>,
        voicePackets: MutableSharedFlow<DecryptedVoicePacket>,
    )
}

private class NewDecryptionDelegate(private val decrypt: Decrypt) : DecryptionDelegate {
    override fun listenForIncoming(
        scope: CoroutineScope,
        udp: VoiceUdpSocket,
        server: SocketAddress,
        audioPackets: MutableSharedFlow<RTPPacket>,
        voicePackets: MutableSharedFlow<DecryptedVoicePacket>,
    ) {
        scope.launch {
            udp.incoming.collect { datagram ->
                if (datagram.address != server) {
                    return@collect
                }
                val voicePacket = decrypt.decrypt(datagram.packet) ?: return@collect
                voicePackets.emit(voicePacket)

                @OptIn(ExperimentalUnsignedTypes::class)
                if (audioPackets.subscriptionCount.value > 0) {
                    val decryptedAudio = voicePacket.decryptedAudio
                    val extension = voicePacket.headerExtension
                    val extensionSize = extension?.let { 4 + it.headerExtension.size * UInt.SIZE_BYTES } ?: 0
                    val data = ByteArray(size = decryptedAudio.size + extensionSize)
                    if (extension != null) {
                        data.writeShortBigEndian(offset = 0, extension.definedByProfile.toShort())
                        data.writeShortBigEndian(offset = 2, extension.headerExtension.size.toShort())
                        extension.headerExtension.forEachIndexed { index, extensionWord ->
                            data.writeIntBigEndian(offset = 4 + index * UInt.SIZE_BYTES, extensionWord.toInt())
                        }
                    }
                    decryptedAudio.copyInto(data, destinationOffset = extensionSize)
                    audioPackets.emit(
                        RTPPacket(
                            paddingBytes = 0u, // TODO explain
                            payloadType = PayloadType.Audio.raw, // TODO explain
                            sequence = voicePacket.sequenceNumber,
                            timestamp = voicePacket.timestamp,
                            ssrc = voicePacket.ssrc,
                            csrcIdentifiers = voicePacket.csrcs.copyOf(),
                            hasMarker = false, // TODO explain
                            hasExtension = extension != null,
                            // TODO explain
                            payload = ByteArrayView.from(data, start = extensionSize, end = data.size)!!,
                        )
                    )
                }
            }
        }
    }
}

private class LegacyDecryptionDelegate(
    private val key: ByteArray,
    private val streams: DefaultStreams,
    private val nonceStrategy: @Suppress("DEPRECATION") NonceStrategy,
) : DecryptionDelegate {
    override fun listenForIncoming(
        scope: CoroutineScope,
        udp: VoiceUdpSocket,
        server: SocketAddress,
        audioPackets: MutableSharedFlow<RTPPacket>,
        voicePackets: MutableSharedFlow<DecryptedVoicePacket>,
    ) = with(streams) {
        scope.listenForIncoming(udp, key, server, audioPackets, nonceStrategy) { rtpPacket ->
            if (voicePackets.subscriptionCount.value > 0) {
                voicePackets.emit(
                    @OptIn(ExperimentalUnsignedTypes::class)
                    DecryptedVoicePacket(
                        sequenceNumber = rtpPacket.sequence,
                        timestamp = rtpPacket.timestamp,
                        ssrc = rtpPacket.ssrc,
                        csrcs = rtpPacket.csrcIdentifiers.copyOf(),
                        headerExtension = null,
                        decryptedAudio = rtpPacket.payload.toByteArray(),
                    )
                )
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun Flow<RTPPacket>.decrypt(nonceStrategy: NonceStrategy, key: ByteArray): Flow<RTPPacket> {
    val codec = XSalsa20Poly1305Codec(key)
    val nonceBuffer = ByteArray(TweetNaclFast.SecretBox.nonceLength).mutableCursor()

    val decryptedBuffer = ByteArray(512)
    val decryptedCursor = decryptedBuffer.mutableCursor()
    val decryptedView = decryptedBuffer.view()

    return mapNotNull {
        nonceBuffer.reset()
        decryptedCursor.reset()

        nonceBuffer.writeByteView(nonceStrategy.strip(it))

        val decrypted = with(it.payload) {
            codec.decrypt(data, dataStart, viewSize, nonceBuffer.data, decryptedCursor)
        }

        if (!decrypted) {
            defaultStreamsLogger.trace { "failed to decrypt the packet with data ${it.payload.data.contentToString()} at offset ${it.payload.dataStart} and length ${it.payload.viewSize - 4}" }
            return@mapNotNull null
        }

        decryptedView.resize(0, decryptedCursor.cursor)

        // mutate the payload data and update the view
        it.payload.data.mutableCursor().writeByteViewOrResize(decryptedView)
        it.payload.resize(0, decryptedView.viewSize)

        it
    }
}

private fun Flow<RTPPacket>.clean(): Flow<RTPPacket> {
    fun processExtensionHeader(payload: ByteArrayView) = with(payload.readableCursor()) {
        consume(Short.SIZE_BYTES) // profile, ignore it
        val countOf32BitWords = readShort() // amount of extension header "words"
        consume((countOf32BitWords * 32) / Byte.SIZE_BITS) // consume extension header

        payload.resize(start = cursor)
    }

    return map { packet ->
        if (packet.hasExtension)
            processExtensionHeader(packet.payload)

        packet
    }
}
