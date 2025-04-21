package dev.kord.voice.streams

import com.iwebpp.crypto.TweetNaclFast
import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.AudioFrame
import dev.kord.voice.encryption.XSalsa20Poly1305Codec
import dev.kord.voice.encryption.strategies.NonceStrategy
import dev.kord.voice.io.*
import dev.kord.voice.udp.PayloadType
import dev.kord.voice.udp.RTPPacket
import dev.kord.voice.udp.VoiceUdpSocket
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.sockets.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*

private val defaultStreamsLogger = KotlinLogging.logger { }

@KordVoice
public class DefaultStreams(
    private val udp: VoiceUdpSocket,
    private val nonceStrategy: NonceStrategy
) : Streams {
    private var s2u: (UInt) -> Snowflake? = { null }

    override suspend fun listen(key: ByteArray, server: SocketAddress, s2u: (UInt) -> Snowflake?): Unit =
        coroutineScope {
            this@DefaultStreams.s2u = s2u

        udp.incoming
            .filter { it.address == server }
            .mapNotNull { RTPPacket.fromPacket(it.packet) }
            .filter { it.payloadType == PayloadType.Audio.raw }
            .decrypt(nonceStrategy, key)
            .clean()
            .onEach { _incomingAudioPackets.emit(it) }
            .launchIn(this)
    }

    private val _incomingAudioPackets: MutableSharedFlow<RTPPacket> = MutableSharedFlow()

    override val incomingAudioPackets: SharedFlow<RTPPacket> = _incomingAudioPackets

    override val incomingAudioFrames: Flow<Pair<UInt, AudioFrame>>
        get() = incomingAudioPackets.map { it.ssrc to AudioFrame(it.payload.toByteArray()) }

    override val incomingUserStreams: Flow<Pair<Snowflake, AudioFrame>>
        get() = incomingAudioFrames.mapNotNull { (ssrc, frame) -> s2u(ssrc)?.to(frame) }
}

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
