package dev.kord.voice.encryption

import com.iwebpp.crypto.TweetNaclFast
import com.iwebpp.crypto.TweetNaclFast.SecretBox.boxzerobytesLength
import com.iwebpp.crypto.TweetNaclFast.SecretBox.zerobytesLength
import dev.kord.voice.io.MutableByteArrayCursor

internal class XSalsa20Poly1305Encryption(private val key: ByteArray) {
    private val m: ByteArray = ByteArray(984)
    private val c: ByteArray = ByteArray(984)

    fun box(message: ByteArray, mOffset: Int, mLength: Int, nonce: ByteArray, output: MutableByteArrayCursor): Boolean {
        m.fill(0)
        c.fill(0)

        for (i in 0 until mLength)
            m[i + zerobytesLength] = message[i + mOffset]

        val messageBufferLength = mLength + zerobytesLength

        if (TweetNaclFast.crypto_secretbox(c, m, messageBufferLength, nonce, key) == 0) {
            output.resize(output.cursor + messageBufferLength - boxzerobytesLength)

            output.writeByteArray(c, boxzerobytesLength, messageBufferLength - boxzerobytesLength)

            // TODO: implement a nonce strategy
            output.writeByteArray(nonce, length = 4)

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