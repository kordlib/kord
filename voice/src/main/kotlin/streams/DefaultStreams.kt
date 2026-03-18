package dev.kord.voice.streams

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.AudioFrame
import dev.kord.voice.encryption.XChaCha20Poly1305Codec
import dev.kord.voice.encryption.strategies.*
import dev.kord.voice.gateway.Speaking
import dev.kord.voice.gateway.VoiceGateway
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view
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
import kotlinx.io.readByteArray

private val defaultStreamsLogger = KotlinLogging.logger { }

@KordVoice
public class DefaultStreams(
    private val voiceGateway: VoiceGateway,
    private val udp: VoiceUdpSocket,
    private val nonceStrategy: @Suppress("DEPRECATION") NonceStrategy
) : Streams {

    public constructor(voiceGateway: VoiceGateway, udp: VoiceUdpSocket) : this(voiceGateway, udp, @Suppress("DEPRECATION") LiteNonceStrategy())

    private suspend fun CoroutineScope.listenForIncoming(key: ByteArray, server: SocketAddress) {
        udp.incoming
            .filter { it.address == server }
            .mapNotNull { RTPPacket.fromPacket(it.packet) }
            .filter { it.payloadType == PayloadType.Audio.raw }
            .decrypt(nonceStrategy, key)
            .strip()
            .onEach { _incomingAudioPackets.emit(it) }
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

    override suspend fun listen(key: ByteArray, server: SocketAddress): Unit = coroutineScope {
        listenForIncoming(key, server)
        listenForUserFrames()
    }

    private val _incomingAudioPackets: MutableSharedFlow<RTPPacket> = MutableSharedFlow()

    override val incomingAudioPackets: SharedFlow<RTPPacket> = _incomingAudioPackets

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

@OptIn(ExperimentalUnsignedTypes::class)
private suspend fun Flow<RTPPacket>.decrypt(nonceStrategy: @Suppress("DEPRECATION") NonceStrategy, key: ByteArray): Flow<RTPPacket> {
    val codec = XChaCha20Poly1305Codec(key)
    codec.init()
    val nonceBuffer = ByteArray(nonceStrategy.nonceLength).mutableCursor()

    val decryptedBuffer = ByteArray(512)
    val decryptedCursor = decryptedBuffer.mutableCursor()
    val decryptedView = decryptedBuffer.view()

    return mapNotNull {
        if (it.source == null) {
            defaultStreamsLogger.error { "no source for incoming packet." }
            return@mapNotNull null
        }

        nonceBuffer.reset()
        decryptedCursor.reset()

        nonceBuffer.writeByteView(nonceStrategy.strip(it))

        val decrypted = with(it.payload) {
            codec.decrypt(data, dataStart, viewSize, it.source, 0, it.unencryptedLength, nonceBuffer.data, decryptedCursor)
        }

        if (!decrypted) {
            defaultStreamsLogger.trace { "failed to decrypt the packet with data ${it.payload.data.contentToString()} at offset ${it.payload.dataStart} and length ${it.payload.viewSize - 4}" }
            return@mapNotNull null
        }

        decryptedView.resize(0, decryptedCursor.cursor)

        // mutate the payload data and update the view
        it.payload.mutableCursor().writeByteView(decryptedView)
        it.payload.resize(end = it.payload.dataStart + decryptedView.viewSize)

        it
    }
}

private fun Flow<RTPPacket>.strip(): Flow<RTPPacket> {
    fun stripExtensionData(packet: RTPPacket) {
        packet.payload.resize(start = packet.payload.dataStart + packet.extensionLengthWords.toInt() * 4)
    }

    return onEach { packet ->
        if (packet.hasExtension)
            stripExtensionData(packet)
    }
}
