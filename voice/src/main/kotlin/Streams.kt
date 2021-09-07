package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.gateway.Speaking
import dev.kord.voice.gateway.on
import dev.kord.voice.udp.AudioPacket
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

@KordVoice
class Streams(
    connection: VoiceConnection,
    dispatcher: CoroutineDispatcher
) : CoroutineScope {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + dispatcher + CoroutineName("Voice Connection Incoming Streams")

    internal val key: AtomicRef<ByteArray?> = atomic(null)

    /**
     * A flow of all incoming [dev.kord.voice.udp.AudioPacket.DecryptedPacket]s through the UDP connection.
     */
    val incomingAudioPackets: SharedFlow<AudioPacket.DecryptedPacket> =
        connection.udp
            .incoming
            .mapNotNull(AudioPacket::encryptedFrom)
            .map { it.decrypt(key.value!!) }
            .shareIn(this, SharingStarted.Lazily)

    /**
     * A flow of all incoming [AudioFrame]s mapped to their ssrc.
     */
    val incomingAudioFrames get() = incomingAudioPackets.map { it.ssrc to AudioFrame.fromData(it.data)!! }

    private val _incomingUserAudioFrames: MutableSharedFlow<Pair<Snowflake, AudioFrame>> = MutableSharedFlow()

    /**
     * A flow of incoming [AudioFrame]s mapped to its corresponding user id. Streams for each user are built over time,
     * or whenever the [dev.kord.voice.gateway.VoiceGateway] receives a [Speaking] event.
     */
    val incomingUserStreams: SharedFlow<Pair<Snowflake, AudioFrame>> = _incomingUserAudioFrames

    private val _ssrcToUser: AtomicRef<MutableMap<UInt, Snowflake>> = atomic(mutableMapOf())

    /**
     * A mapping of ssrc to user id.
     * This cache is built over time through the [dev.kord.voice.gateway.VoiceGateway].
     */
    val ssrcToUser: Map<UInt, Snowflake> by _ssrcToUser

    init {
        connection.voiceGateway.on<Speaking>(scope = this) {
            _ssrcToUser.update {
                it.computeIfAbsent(ssrc) {
                    incomingAudioFrames
                        .filter { (ssrc, _) -> ssrc == this@on.ssrc }
                        .map { (_, frame) -> userId to frame }
                        .onEach { _incomingUserAudioFrames.emit(it) }
                        .launchIn(this@Streams)

                    userId
                }

                it
            }
        }
    }
}