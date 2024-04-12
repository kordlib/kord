package dev.kord.voice.udp

import com.ionspin.kotlin.crypto.secretbox.SecretBox
import com.ionspin.kotlin.crypto.secretbox.crypto_secretbox_NONCEBYTES
import dev.kord.voice.encryption.strategies.NonceStrategy
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

@Suppress("FunctionName")
public actual fun DefaultAudioPacketProvider(key: ByteArray, nonceStrategy: NonceStrategy): AudioPacketProvider =
    DefaultNativeAudioPacketProvider(key, nonceStrategy)

public class DefaultNativeAudioPacketProvider(key: ByteArray, nonceStrategy: NonceStrategy) :
    AudioPacketProvider(key, nonceStrategy) {

    private val packetBuffer = ByteArray(2048)
    private val packetBufferCursor: MutableByteArrayCursor = packetBuffer.mutableCursor()
    private val packetBufferView: ByteArrayView = packetBuffer.view()

    private val rtpHeaderView: ByteArrayView = packetBuffer.view(0, RTP_HEADER_LENGTH)!!

    private val nonceBuffer: MutableByteArrayCursor = ByteArray(crypto_secretbox_NONCEBYTES).mutableCursor()

    private val lock = SynchronizedObject()

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun provide(sequence: UShort, timestamp: UInt, ssrc: UInt, data: ByteArray): ByteArrayView =
        synchronized(lock) {
            with(packetBufferCursor) {
                this.reset()
                nonceBuffer.reset()

                // make sure we enough room in this buffer
                resize(RTP_HEADER_LENGTH + (data.size + 16) + nonceStrategy.nonceLength)

                // write header and generate nonce
                writeHeader(sequence.toShort(), timestamp.toInt(), ssrc.toInt())

                val rawNonce = nonceStrategy.generate { rtpHeaderView }
                nonceBuffer.writeByteView(rawNonce)

                // encrypt data and write into our buffer
                try {
                    writeByteArray(
                        SecretBox.easy(
                            data.toUByteArray(),
                            nonceBuffer.data.toUByteArray(),
                            key.toUByteArray()
                        ).toByteArray()
                    )
                } catch (e: Throwable) {
                    throw CouldNotEncryptDataException(data, e)
                }

                nonceStrategy.append(rawNonce, this)

                // let's make sure we have the correct view of the packet
                if (!packetBufferView.resize(0, cursor)) error("couldn't resize packet buffer view?!")

                packetBufferView
            }
        }
}
