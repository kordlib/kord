package dev.kord.voice

import com.codahale.xsalsa20poly1305.SecretBox
import okio.ByteString

internal object XSalsa20Poly1305Encoder {
    internal fun encode(message: ByteArray, key: ByteArray, nonce: ByteArray): ByteArray =
        SecretBox(ByteString.of(*key)).seal(ByteString.of(*nonce), ByteString.of(*message)).toByteArray()
}