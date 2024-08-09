package dev.kord.voice.streams

import com.ionspin.kotlin.crypto.secretbox.SecretBox
import com.ionspin.kotlin.crypto.secretbox.crypto_secretbox_NONCEBYTES
import dev.kord.voice.encryption.strategies.NonceStrategy
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view
import dev.kord.voice.udp.RTPPacket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

@OptIn(ExperimentalUnsignedTypes::class)
internal actual fun Flow<RTPPacket>.decrypt(nonceStrategy: NonceStrategy, key: ByteArray): Flow<RTPPacket> {
    val nonceBuffer = ByteArray(crypto_secretbox_NONCEBYTES).mutableCursor()
    val uKey = key.asUByteArray()

    return mapNotNull {
        nonceBuffer.reset()
        nonceBuffer.writeByteView(nonceStrategy.strip(it))

        val decrypted = SecretBox.openEasy(
            it.payload.toByteArray().asUByteArray(),
            nonceBuffer.data.asUByteArray(),
            uKey
        )

        it.copy(payload = decrypted.asByteArray().view())
    }
}
