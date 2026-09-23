package dev.kord.voice.encryption

import dev.kord.voice.io.MutableByteArrayCursor
import io.github.oshai.kotlinlogging.KotlinLogging
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private val aeadLogger = KotlinLogging.logger { }

/**
 * AES-256-GCM encryption for Discord voice transport.
 * Used with the `aead_aes256_gcm_rtpsize` encryption mode.
 *
 * Unlike XSalsa20-Poly1305, AEAD modes use the RTP header as Additional Authenticated Data (AAD),
 * meaning the header is authenticated but not encrypted.
 */
internal class AeadEncryption(key: ByteArray) {
    private val secretKey = SecretKeySpec(key, "AES")

    // Cipher instances are NOT thread-safe — use separate instances for encrypt/decrypt
    // to allow concurrent send/receive without synchronization.
    private val encryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
    private val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")

    internal companion object {
        const val NONCE_BYTES: Int = 4
        const val EXPANDED_NONCE_BYTES: Int = 12
        const val TAG_BYTES: Int = 16
        const val TAG_BITS: Int = 128
    }

    /**
     * Encrypt audio payload using AES-256-GCM.
     *
     * @param plaintext the audio data to encrypt
     * @param plaintextOffset offset into plaintext array
     * @param plaintextLength length of plaintext data
     * @param nonce the 12-byte expanded nonce
     * @param aad Additional Authenticated Data (RTP header)
     * @param aadOffset offset into AAD array
     * @param aadLength length of AAD data
     * @param output cursor to write ciphertext + tag into
     * @return true if encryption succeeded
     */
    fun encrypt(
        plaintext: ByteArray,
        plaintextOffset: Int,
        plaintextLength: Int,
        nonce: ByteArray,
        aad: ByteArray,
        aadOffset: Int,
        aadLength: Int,
        output: MutableByteArrayCursor,
    ): Boolean {
        return try {
            val spec = GCMParameterSpec(TAG_BITS, nonce)
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
            encryptCipher.updateAAD(aad, aadOffset, aadLength)

            // Write directly into the output buffer to avoid intermediate allocation
            val outputSize = encryptCipher.getOutputSize(plaintextLength)
            output.resize(output.cursor + outputSize)
            val bytesWritten = encryptCipher.doFinal(
                plaintext, plaintextOffset, plaintextLength,
                output.data, output.cursor,
            )
            output.cursor += bytesWritten
            true
        } catch (e: Exception) {
            aeadLogger.trace { "AEAD encrypt failed: ${e.message}" }
            false
        }
    }

    /**
     * Decrypt audio payload using AES-256-GCM.
     *
     * @param ciphertextWithTag the encrypted data including GCM auth tag
     * @param ciphertextOffset offset into ciphertext array
     * @param ciphertextLength length of ciphertext + tag data
     * @param nonce the 12-byte expanded nonce
     * @param aad Additional Authenticated Data (RTP header)
     * @param aadOffset offset into AAD array
     * @param aadLength length of AAD data
     * @param output cursor to write plaintext into
     * @return true if decryption succeeded (tag verified)
     */
    fun decrypt(
        ciphertextWithTag: ByteArray,
        ciphertextOffset: Int,
        ciphertextLength: Int,
        nonce: ByteArray,
        aad: ByteArray,
        aadOffset: Int,
        aadLength: Int,
        output: MutableByteArrayCursor,
    ): Boolean {
        return try {
            val spec = GCMParameterSpec(TAG_BITS, nonce)
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            decryptCipher.updateAAD(aad, aadOffset, aadLength)

            // Write directly into the output buffer to avoid intermediate allocation
            val outputSize = decryptCipher.getOutputSize(ciphertextLength)
            output.resize(output.cursor + outputSize)
            val bytesWritten = decryptCipher.doFinal(
                ciphertextWithTag, ciphertextOffset, ciphertextLength,
                output.data, output.cursor,
            )
            output.cursor += bytesWritten
            true
        } catch (e: Exception) {
            aeadLogger.trace { "AEAD decrypt failed: ${e.message}" }
            false
        }
    }
}

/**
 * Expand a 4-byte nonce to 12 bytes for AES-GCM by zero-padding.
 * The 4-byte nonce occupies the first 4 bytes, remaining 8 bytes are zero.
 */
internal fun expandNonce(nonce4: ByteArray, expanded: ByteArray) {
    nonce4.copyInto(expanded, 0, 0, 4)
    for (i in 4 until 12) expanded[i] = 0
}
