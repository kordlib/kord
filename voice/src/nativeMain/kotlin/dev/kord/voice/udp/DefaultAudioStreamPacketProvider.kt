package dev.kord.voice.udp

import com.ionspin.kotlin.crypto.secretbox.SecretBox
import com.ionspin.kotlin.crypto.secretbox.crypto_secretbox_MACBYTES
import com.ionspin.kotlin.crypto.secretbox.crypto_secretbox_NONCEBYTES
import dev.kord.voice.encryption.strategies.NonceStrategy
import dev.kord.voice.io.*
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

public actual class DefaultAudioPacketProvider actual constructor(key: ByteArray, nonceStrategy: NonceStrategy) :
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
                resize(RTP_HEADER_LENGTH + (data.size + crypto_secretbox_MACBYTES) + nonceStrategy.nonceLength)

                // write header and generate nonce
                writeHeader(sequence.toShort(), timestamp.toInt(), ssrc.toInt())

                val rawNonce = nonceStrategy.generate { rtpHeaderView }
                nonceBuffer.writeByteView(rawNonce)

                // encrypt data and write into our buffer
                val encrypted = SecretBox.easy(data.asUByteArray(), nonceBuffer.data.asUByteArray(), key.asUByteArray())
                    .asByteArray()
                writeByteArrayOrResize(encrypted)

                nonceStrategy.append(rawNonce, this)

                // let's make sure we have the correct view of the packet
                if (!packetBufferView.resize(0, cursor)) error("couldn't resize packet buffer view?!")

                packetBufferView
            }
        }
}
