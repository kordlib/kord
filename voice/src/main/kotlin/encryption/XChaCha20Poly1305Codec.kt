package dev.kord.voice.encryption

import dev.kord.voice.io.MutableByteArrayCursor

public class XChaCha20Poly1305Codec(public val key: ByteArray) {
    private val encryption = XChaCha20Poly1305Encryption(key)

    public suspend fun init() {
        encryption.init()
    }

    public fun encrypt(
        message: ByteArray,
        mOffset: Int = 0,
        mLength: Int = message.size,
        aad: ByteArray,
        aadOffset: Int = 0,
        aadLength: Int = aad.size,
        nonce: ByteArray,
        output: MutableByteArrayCursor
    ): Boolean =
        encryption.seal(message, mOffset, mLength, nonce, aad, aadOffset, aadLength, output)

    public fun decrypt(
        box: ByteArray,
        boxOffset: Int = 0,
        boxLength: Int = box.size,
        aad: ByteArray,
        aadOffset: Int = 0,
        aadLength: Int = aad.size,
        nonce: ByteArray,
        output: MutableByteArrayCursor
    ): Boolean =
        encryption.open(box, boxOffset, boxLength, nonce, aad, aadOffset, aadLength, output)
}