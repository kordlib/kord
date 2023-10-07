package dev.kord.voice.encryption

import com.iwebpp.crypto.TweetNaclFast
import com.iwebpp.crypto.TweetNaclFast.crypto_secretbox
import com.iwebpp.crypto.TweetNaclFast.crypto_secretbox_open
import dev.kord.voice.io.MutableByteArrayCursor

// https://datatracker.ietf.org/doc/html/rfc6716#section-3.2.1
private const val OPUS_MAX_LENGTH = 1276

internal class XSalsa20Poly1305Encryption(private val key: ByteArray) {
    // this class is only used internally and is used for encrypting opus packets.
    // we can know the maximum sized buffer required to store any opus packet.
    private val m: ByteArray = ByteArray(OPUS_MAX_LENGTH + TweetNaclFast.SecretBox.zerobytesLength)
    private val c: ByteArray = ByteArray(OPUS_MAX_LENGTH + TweetNaclFast.SecretBox.zerobytesLength)

    fun box(message: ByteArray, mOffset: Int, mLength: Int, nonce: ByteArray, output: MutableByteArrayCursor): Boolean {
        m.fill(0)
        c.fill(0)

        for (i in 0 until mLength)
            m[i + TweetNaclFast.SecretBox.zerobytesLength] = message[i + mOffset]

        val messageBufferLength = mLength + TweetNaclFast.SecretBox.zerobytesLength

        if (crypto_secretbox(c, m, messageBufferLength, nonce, key) == 0) {
            output.resize(output.cursor + messageBufferLength - TweetNaclFast.SecretBox.boxzerobytesLength)

            output.writeByteArray(c, TweetNaclFast.SecretBox.boxzerobytesLength, messageBufferLength - TweetNaclFast.SecretBox.boxzerobytesLength)

            return true
        }

        return false
    }

    fun open(box: ByteArray, boxOffset: Int, boxLength: Int, nonce: ByteArray, output: MutableByteArrayCursor): Boolean {
        c.fill(0)
        m.fill(0)

        for (i in 0 until boxLength)
            c[i + TweetNaclFast.SecretBox.boxzerobytesLength] = box[i + boxOffset]

        val cipherLength = boxLength + TweetNaclFast.SecretBox.boxzerobytesLength

        if (crypto_secretbox_open(m, c, cipherLength, nonce, key) == 0) {
            output.resize(output.cursor + cipherLength - TweetNaclFast.SecretBox.zerobytesLength)
            output.writeByteArray(m, TweetNaclFast.Box.zerobytesLength, cipherLength - TweetNaclFast.SecretBox.zerobytesLength)

            return true
        }

        return false
    }
}
