package dev.kord.voice.encryption

import com.iwebpp.crypto.TweetNaclFast
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.mutableCursor

private const val DEPRECATION_SALSA = "This is no longer used by Kord and all voice encryption modes using XSalsa20Poly1305 are no longer supported by Discord. This will be removed in the future."

@Deprecated(DEPRECATION_SALSA)
public class XSalsa20Poly1305Codec(public val key: ByteArray) {
    private val encryption = XSalsa20Poly1305Encryption(key)

    public fun encrypt(
        message: ByteArray,
        mOffset: Int = 0,
        mLength: Int = message.size,
        nonce: ByteArray,
        output: MutableByteArrayCursor
    ): Boolean =
        encryption.box(message, mOffset, mLength, nonce, output)

    public fun decrypt(
        box: ByteArray,
        boxOffset: Int = 0,
        boxLength: Int = box.size,
        nonce: ByteArray,
        output: MutableByteArrayCursor
    ): Boolean =
        encryption.open(box, boxOffset, boxLength, nonce, output)
}

@Deprecated(DEPRECATION_SALSA)
public fun @Suppress("DEPRECATION") XSalsa20Poly1305Codec.encrypt(
    message: ByteArray,
    mOffset: Int = 0,
    mLength: Int = message.size,
    nonce: ByteArray
): ByteArray? {
    val buffer = ByteArray(mLength + TweetNaclFast.SecretBox.boxzerobytesLength)
    if (!encrypt(message, mOffset, mLength, nonce, buffer.mutableCursor())) return null
    return buffer
}

@Deprecated(DEPRECATION_SALSA)
public fun @Suppress("DEPRECATION") XSalsa20Poly1305Codec.decrypt(
    box: ByteArray,
    boxOffset: Int = 0,
    boxLength: Int = box.size,
    nonce: ByteArray
): ByteArray? {
    val buffer = ByteArray(boxLength - TweetNaclFast.SecretBox.boxzerobytesLength)
    if (!decrypt(box, boxOffset, boxLength, nonce, buffer.mutableCursor())) return null
    return buffer
}