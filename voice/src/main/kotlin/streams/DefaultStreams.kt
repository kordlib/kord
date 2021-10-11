package dev.kord.voice.streams

import com.iwebpp.crypto.TweetNaclFast
import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.AudioFrame
import dev.kord.voice.encryption.XSalsa20Poly1305Codec
import dev.kord.voice.gateway.Speaking
import dev.kord.voice.gateway.VoiceGateway
import dev.kord.voice.io.*
import dev.kord.voice.udp.PayloadType
import dev.kord.voice.udp.VoiceUdpSocket
import io.ktor.util.network.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import udp.RTPPacket
import kotlin.coroutines.CoroutineContext

private val defaultStreamsLogger = KotlinLogging.logger { }

@KordVoice
class DefaultStreams(
    voiceGateway: VoiceGateway,
    private val udp: VoiceUdpSocket,
    dispatcher: CoroutineDispatcher,
) : Streams, CoroutineScope {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + dispatcher + CoroutineName("Voice Connection Incoming Streams")

    override suspend fun listen(key: ByteArray, server: NetworkAddress) {
        udp.incoming
            .filter { it.address == server }
            .mapNotNull { RTPPacket.fromPacket(it.packet) }
            .filter { it.payloadType == PayloadType.Audio.raw }
            .decrypt(key)
            .clean()
            .onEach { _incomingAudioPackets.emit(it) }
            .launchIn(this)
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

    override val ssrcToUser: Map<UInt, Snowflake> by _ssrcToUser

    init {
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
}

private fun Flow<RTPPacket>.decrypt(key: ByteArray): Flow<RTPPacket> {
    val codec = XSalsa20Poly1305Codec(key)
    val nonceBuffer = ByteArray(TweetNaclFast.SecretBox.nonceLength).mutableCursor()

    val decryptedBuffer = ByteArray(512)
    val decryptedCursor = decryptedBuffer.mutableCursor()
    val decryptedView = decryptedBuffer.view()

    return mapNotNull {
        nonceBuffer.reset()
        nonceBuffer.writeByteArray(it.payload.data, it.payload.dataEnd - 4, 4)

        decryptedCursor.reset()
        val decrypted = with(it.payload) { codec.decrypt(data, dataStart, viewSize - 4, nonceBuffer.data, decryptedCursor) }

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
