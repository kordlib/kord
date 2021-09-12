package dev.kord.voice.streams

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.AudioFrame
import dev.kord.voice.gateway.Speaking
import dev.kord.voice.gateway.VoiceGateway
import dev.kord.voice.rtp.AudioPacket
import dev.kord.voice.rtp.PayloadType
import dev.kord.voice.udp.VoiceUdpConnection
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import okio.Buffer
import kotlin.coroutines.CoroutineContext

@KordVoice
class DefaultStreams(
    voiceGateway: VoiceGateway,
    udp: VoiceUdpConnection,
    dispatcher: CoroutineDispatcher
) : Streams, CoroutineScope {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + dispatcher + CoroutineName("Voice Connection Incoming Streams")

    override var key: ByteArray? by atomic(null)

    override val incomingAudioPackets: SharedFlow<AudioPacket.DecryptedPacket> =
        udp.incoming
            .map { it.copy() }
            .mapNotNull(AudioPacket::encryptedFrom)
            .filter { it.payloadType == PayloadType.Audio }
            .map { it.decrypt(key!!).removeExtensionHeader() }
            .shareIn(this, SharingStarted.Lazily)

    // perhaps we can expose the extension header later
    @OptIn(ExperimentalUnsignedTypes::class)
    fun AudioPacket.DecryptedPacket.removeExtensionHeader(): AudioPacket.DecryptedPacket {
        fun processExtensionHeader(data: ByteArray): ByteArray = with(Buffer()) {
            buffer.write(data)
            readShort() // profile, ignore it
            val countOf32BitWords = readShort().toLong() // amount of extension header "words"
            readByteArray((countOf32BitWords * 32)/Byte.SIZE_BITS) // consume extension header

            readByteArray() // consume rest of payload and return it
        }

        return if(packet.hasExtension)
            AudioPacket.DecryptedPacket(packet.copy(hasExtension = false, payload = processExtensionHeader(packet.payload)))
        else this
    }

    override val incomingAudioFrames: Flow<Pair<UInt, AudioFrame>>
        get() = incomingAudioPackets.map { it.ssrc to AudioFrame.fromData(it.payload)!! }

    private val _incomingUserAudioFrames: MutableSharedFlow<Pair<Snowflake, AudioFrame>> = MutableSharedFlow()

    override val incomingUserStreams: SharedFlow<Pair<Snowflake, AudioFrame>> = _incomingUserAudioFrames

    private val _ssrcToUser: AtomicRef<MutableMap<UInt, Snowflake>> = atomic(mutableMapOf())

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