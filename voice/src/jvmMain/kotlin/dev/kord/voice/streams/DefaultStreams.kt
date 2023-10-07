@file:JvmName("DefaultStreamsJvm")

package dev.kord.voice.streams

import com.iwebpp.crypto.TweetNaclFast
import dev.kord.voice.encryption.XSalsa20Poly1305Codec
import dev.kord.voice.encryption.strategies.NonceStrategy
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view
import dev.kord.voice.io.writeByteViewOrResize
import dev.kord.voice.udp.RTPPacket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

internal actual fun Flow<RTPPacket>.decrypt(nonceStrategy: NonceStrategy, key: ByteArray): Flow<RTPPacket> {
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
        decryptedView.toByteArray()

        // mutate the payload data and update the view
        it.payload.data.mutableCursor().writeByteViewOrResize(decryptedView)
        it.payload.resize(0, decryptedView.viewSize)

        it
    }
}
