package dev.kord.voice.encryption

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna
import dev.kord.voice.io.MutableByteArrayCursor

// https://datatracker.ietf.org/doc/html/rfc6716#section-3.2.1
private const val OPUS_MAX_LENGTH = 1276

internal class XChaCha20Poly1305Encryption(private val key: ByteArray) {
    companion object {
        const val KEY_LENGTH = 32
        const val NONCE_LENGTH = 24
        const val TAG_LENGTH = 16
    }

    init {
        require(key.size == KEY_LENGTH) { "XChaCha20Poly1305 key must be $KEY_LENGTH bytes" }
    }

    suspend fun init() {
        LibsodiumInitializer.initialize()
    }

    private val m: ByteArray = ByteArray(OPUS_MAX_LENGTH + TAG_LENGTH)
    private val a: ByteArray = ByteArray(512)
    private val c: ByteArray = ByteArray(OPUS_MAX_LENGTH + TAG_LENGTH)
    private val n: ByteArray = ByteArray(NONCE_LENGTH)

    fun seal(
        message: ByteArray,
        mOffset: Int,
        mLength: Int,
        nonce: ByteArray,
        aad: ByteArray,
        aadOffset: Int,
        aadLength: Int,
        output: MutableByteArrayCursor
    ): Boolean {
        message.copyInto(m, startIndex = mOffset, endIndex = mOffset + mLength)
        aad.copyInto(a, startIndex = aadOffset, endIndex = aadOffset + aadLength)
        nonce.copyInto(n)

        val rx = sodiumJna.crypto_aead_xchacha20poly1305_ietf_encrypt(
            c,
            null,
            m, mLength.toLong(),
            a, aadLength.toLong(),
            null,
            n,
            key
        )

        if (rx != 0) return false

        output.resize(output.cursor + mLength + TAG_LENGTH)
        output.writeByteArray(c, 0, mLength + TAG_LENGTH)

        return true
    }

    fun open(
        box: ByteArray,
        boxOffset: Int,
        boxLength: Int,
        nonce: ByteArray,
        aad: ByteArray,
        aadOffset: Int,
        aadLength: Int,
        output: MutableByteArrayCursor
    ): Boolean {
        box.copyInto(c, startIndex = boxOffset, endIndex = boxOffset + boxLength)
        aad.copyInto(a, startIndex = aadOffset, endIndex = aadOffset + aadLength)
        nonce.copyInto(n)

        val rx = sodiumJna.crypto_aead_xchacha20poly1305_ietf_decrypt(
            message = m, messageLength = null, nsec = null,
            ciphertext = c, ciphertextLength = boxLength.toLong(),
            additionalData = a, additionalDataLength = aadLength.toLong(),
            npub = n, key = key
        )

        if (rx != 0) return false

        output.resize(output.cursor + boxLength - TAG_LENGTH)
        output.writeByteArray(m, 0, boxLength - TAG_LENGTH)

        return true
    }
}