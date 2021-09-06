package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.gateway.Speaking
import dev.kord.voice.gateway.on
import dev.kord.voice.udp.AudioPacket
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

@KordVoice
class Streams(
    connection: VoiceConnection,
    dispatcher: CoroutineDispatcher
) : CoroutineScope {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + dispatcher + CoroutineName("Voice Connection Incoming Streams")

    // this will be set before it is used as the key is received before the udp connection is even established
    internal lateinit var key: ByteArray

    /**
     * A flow of all incoming [dev.kord.voice.udp.AudioPacket.DecryptedPacket]s through the UDP connection.
     */
    val incomingAudioPackets: SharedFlow<AudioPacket.DecryptedPacket> =
        connection.udp
            .incoming
            .mapNotNull(AudioPacket::encryptedFrom)
            .map { it.decrypt(key) }
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

    private val _ssrcToUser: MutableMap<UInt, Snowflake> = mutableMapOf()

    /**
     * A mapping of ssrc to user id.
     * This cache is built over time through the [dev.kord.voice.gateway.VoiceGateway].
     */
    val ssrcToUser: Map<UInt, Snowflake> get() = _ssrcToUser

    init {
        connection.voiceGateway.on<Speaking> {
            if (ssrcToUser[ssrc] == null) {
                _ssrcToUser[ssrc] = userId

                launch {
                    _incomingUserAudioFrames.emitAll(
                        incomingAudioFrames
                            .filter { (ssrc, _) -> ssrc == this@on.ssrc }
                            .map { (_, frame) -> userId to frame }
                    )
                }
            }
        }
    }
}