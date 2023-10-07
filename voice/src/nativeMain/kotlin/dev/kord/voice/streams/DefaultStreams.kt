package dev.kord.voice.streams

import com.ionspin.kotlin.crypto.secretbox.SecretBox
import com.ionspin.kotlin.crypto.secretbox.SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey
import com.ionspin.kotlin.crypto.secretbox.crypto_secretbox_NONCEBYTES
import dev.kord.voice.encryption.strategies.NonceStrategy
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.writeByteArrayOrResize
import dev.kord.voice.udp.RTPPacket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
@OptIn(ExperimentalUnsignedTypes::class)
internal actual fun Flow<RTPPacket>.decrypt(nonceStrategy: NonceStrategy, key: ByteArray): Flow<RTPPacket> {
    val nonceBuffer = ByteArray(crypto_secretbox_NONCEBYTES).mutableCursor()

    val decryptedBuffer = ByteArray(512)
    val decryptedCursor = decryptedBuffer.mutableCursor()

    return mapNotNull {
        nonceBuffer.reset()
        decryptedCursor.reset()

        nonceBuffer.writeByteView(nonceStrategy.strip(it))

        val decrypted = try {
            with(it.payload) {
                SecretBox.openEasy(toByteArray().asUByteArray(), nonceBuffer.data.asUByteArray(), key.asUByteArray())
                    .asByteArray()
            }
        } catch (exception: SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey) {
            defaultStreamsLogger.trace { "failed to decrypt the packet with data ${it.payload.data.contentToString()} at offset ${it.payload.dataStart} and length ${it.payload.viewSize - 4}" }
            return@mapNotNull null
        }

        // mutate the payload data and update the view
        it.payload.data.mutableCursor().writeByteArrayOrResize(decrypted)
        it.payload.resize(0, decrypted.size)

        it
    }
}
