package dev.kord.voice.encryption

import com.iwebpp.crypto.TweetNaclFast
import com.iwebpp.crypto.TweetNaclFast.SecretBox.boxzerobytesLength
import com.iwebpp.crypto.TweetNaclFast.SecretBox.zerobytesLength
import dev.kord.voice.io.MutableByteArrayCursor

// https://datatracker.ietf.org/doc/html/rfc6716#section-3.2.1
private const val OPUS_MAX_LENGTH = 1276

internal class XSalsa20Poly1305Encryption(private val key: ByteArray) {
    // this class is only used internally and is used for encrypting opus packets.
    // we can know the maximum sized buffer required to store any opus packet.
    private val m: ByteArray = ByteArray(OPUS_MAX_LENGTH + zerobytesLength)
    private val c: ByteArray = ByteArray(OPUS_MAX_LENGTH + zerobytesLength)

    fun box(message: ByteArray, mOffset: Int, mLength: Int, nonce: ByteArray, output: MutableByteArrayCursor): Boolean {
        m.fill(0)
        c.fill(0)

        for (i in 0 until mLength)
            m[i + zerobytesLength] = message[i + mOffset]

        val messageBufferLength = mLength + zerobytesLength

        if (TweetNaclFast.crypto_secretbox(c, m, messageBufferLength, nonce, key) == 0) {
            output.resize(output.cursor + messageBufferLength - boxzerobytesLength)

            output.writeByteArray(c, boxzerobytesLength, messageBufferLength - boxzerobytesLength)

            return true
        }

        return false
    }

    fun open(box: ByteArray, boxOffset: Int, boxLength: Int, nonce: ByteArray, output: MutableByteArrayCursor): Boolean {
        c.fill(0)
        m.fill(0)

        for (i in 0 until boxLength)
            c[i + boxzerobytesLength] = box[i + boxOffset]

        val cipherLength = boxLength + TweetNaclFast.Box.boxzerobytesLength

        if (TweetNaclFast.crypto_secretbox_open(m, c, cipherLength, nonce, key) == 0) {
            output.resize(output.cursor + cipherLength - zerobytesLength)
            output.writeByteArray(m, TweetNaclFast.Box.zerobytesLength, cipherLength - zerobytesLength)

            return true
        }

        return false
    }
}