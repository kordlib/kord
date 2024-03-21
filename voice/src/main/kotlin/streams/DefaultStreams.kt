package dev.kord.voice.streams

import com.iwebpp.crypto.TweetNaclFast
import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.AudioFrame
import dev.kord.voice.encryption.VoiceEncryption
import dev.kord.voice.gateway.Speaking
import dev.kord.voice.gateway.VoiceGateway
import dev.kord.voice.io.*
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

private val defaultStreamsLogger = KotlinLogging.logger { }

@KordVoice
public class DefaultStreams(
    private val voiceGateway: VoiceGateway,
    private val udp: VoiceUdpSocket,
    private val encryption: VoiceEncryption,
) : Streams {
    private fun CoroutineScope.listenForIncoming(key: ByteArray, server: SocketAddress) {
        udp.incoming
            .filter { it.address == server }
            .mapNotNull { RTPPacket.fromPacket(it.packet) }
            .filter { it.payloadType == PayloadType.Audio.raw }
            .decrypt(encryption, key)
            .clean()
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

private fun Flow<RTPPacket>.decrypt(encryption: VoiceEncryption, key: ByteArray): Flow<RTPPacket> {
    val unbox = encryption.createUnbox(key)
    val nonceBuffer = ByteArray(encryption.nonceLength).mutableCursor()

    val decryptedBuffer = ByteArray(512)
    val decryptedCursor = decryptedBuffer.mutableCursor()
    val decryptedView = decryptedBuffer.view()

    return mapNotNull { packet ->
        nonceBuffer.reset()
        decryptedCursor.reset()

        nonceBuffer.writeByteView(unbox.getNonce(packet))

        val decrypted = with(packet.payload) {
            unbox.decrypt(data, dataStart, viewSize, nonceBuffer.data, decryptedCursor)
        }

        if (!decrypted) {
            defaultStreamsLogger.trace { "failed to decrypt the packet with data ${packet.payload.data.contentToString()} at offset ${packet.payload.dataStart} and length ${packet.payload.viewSize - 4}" }
            return@mapNotNull null
        }

        decryptedView.resize(0, decryptedCursor.cursor)

        // mutate the payload data and update the view
        packet.payload.data.mutableCursor().writeByteViewOrResize(decryptedView)
        packet.payload.resize(0, decryptedView.viewSize)

        packet
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
