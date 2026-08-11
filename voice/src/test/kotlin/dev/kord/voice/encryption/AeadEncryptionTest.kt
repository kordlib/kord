package dev.kord.voice.encryption

import dev.kord.voice.io.MutableByteArrayCursor
import kotlin.test.*
import org.junit.jupiter.api.Nested

class AeadEncryptionTest {

    private fun createKey(): ByteArray = ByteArray(32) { it.toByte() } // 32 bytes for AES-256

    private fun createNonce12(): ByteArray = ByteArray(12) { (it + 1).toByte() }

    private fun createAad(): ByteArray = byteArrayOf(0x80.toByte(), 0x78, 0x00, 0x01, 0x00, 0x00, 0x00, 0xA0.toByte(), 0x00, 0x00, 0x00, 0x01)

    @Nested
    inner class EncryptDecryptRoundtrip {
        @Test
        fun `encrypt then decrypt returns original plaintext`() {
            val key = createKey()
            val encryption = AeadEncryption(key)
            val plaintext = byteArrayOf(10, 20, 30, 40, 50, 60, 70, 80)
            val nonce = createNonce12()
            val aad = createAad()

            // Encrypt
            val encOutput = MutableByteArrayCursor(ByteArray(plaintext.size + AeadEncryption.TAG_BYTES + 16))
            val encResult = encryption.encrypt(plaintext, 0, plaintext.size, nonce, aad, 0, aad.size, encOutput)
            assertTrue(encResult, "Encryption should succeed")

            val ciphertextLength = encOutput.cursor

            // Decrypt
            val decOutput = MutableByteArrayCursor(ByteArray(ciphertextLength + 16))
            val decResult = encryption.decrypt(encOutput.data, 0, ciphertextLength, nonce, aad, 0, aad.size, decOutput)
            assertTrue(decResult, "Decryption should succeed")

            val decrypted = decOutput.data.copyOfRange(0, decOutput.cursor)
            assertContentEquals(plaintext, decrypted)
        }

        @Test
        fun `encrypted data differs from plaintext`() {
            val key = createKey()
            val encryption = AeadEncryption(key)
            val plaintext = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            val nonce = createNonce12()
            val aad = createAad()

            val encOutput = MutableByteArrayCursor(ByteArray(plaintext.size + AeadEncryption.TAG_BYTES + 16))
            encryption.encrypt(plaintext, 0, plaintext.size, nonce, aad, 0, aad.size, encOutput)

            val ciphertext = encOutput.data.copyOfRange(0, encOutput.cursor)
            // Ciphertext + tag should be longer than plaintext
            assertTrue(ciphertext.size > plaintext.size, "Ciphertext with tag should be longer than plaintext")
            // Content should differ
            assertFalse(plaintext.contentEquals(ciphertext.copyOfRange(0, plaintext.size)),
                "Encrypted content should differ from plaintext")
        }

        @Test
        fun `empty plaintext encrypt and decrypt roundtrip`() {
            val key = createKey()
            val encryption = AeadEncryption(key)
            val plaintext = byteArrayOf()
            val nonce = createNonce12()
            val aad = createAad()

            val encOutput = MutableByteArrayCursor(ByteArray(AeadEncryption.TAG_BYTES + 16))
            val encResult = encryption.encrypt(plaintext, 0, 0, nonce, aad, 0, aad.size, encOutput)
            assertTrue(encResult, "Encryption of empty plaintext should succeed")
            // Should produce at least the GCM tag
            assertTrue(encOutput.cursor > 0, "Should produce GCM auth tag even for empty plaintext")

            val decOutput = MutableByteArrayCursor(ByteArray(encOutput.cursor + 16))
            val decResult = encryption.decrypt(encOutput.data, 0, encOutput.cursor, nonce, aad, 0, aad.size, decOutput)
            assertTrue(decResult, "Decryption of empty plaintext should succeed")
            assertEquals(0, decOutput.cursor, "Decrypted empty plaintext should have 0 bytes")
        }
    }

    @Nested
    inner class DecryptionFailures {
        @Test
        fun `decrypt with wrong key fails`() {
            val key1 = ByteArray(32) { it.toByte() }
            val key2 = ByteArray(32) { (it + 100).toByte() }
            val enc = AeadEncryption(key1)
            val dec = AeadEncryption(key2)

            val plaintext = byteArrayOf(1, 2, 3, 4, 5)
            val nonce = createNonce12()
            val aad = createAad()

            val encOutput = MutableByteArrayCursor(ByteArray(plaintext.size + AeadEncryption.TAG_BYTES + 16))
            enc.encrypt(plaintext, 0, plaintext.size, nonce, aad, 0, aad.size, encOutput)

            val decOutput = MutableByteArrayCursor(ByteArray(encOutput.cursor + 16))
            val result = dec.decrypt(encOutput.data, 0, encOutput.cursor, nonce, aad, 0, aad.size, decOutput)
            assertFalse(result, "Decryption with wrong key should fail")
        }

        @Test
        fun `decrypt with tampered ciphertext fails`() {
            val key = createKey()
            val encryption = AeadEncryption(key)
            val plaintext = byteArrayOf(1, 2, 3, 4, 5)
            val nonce = createNonce12()
            val aad = createAad()

            val encOutput = MutableByteArrayCursor(ByteArray(plaintext.size + AeadEncryption.TAG_BYTES + 16))
            encryption.encrypt(plaintext, 0, plaintext.size, nonce, aad, 0, aad.size, encOutput)

            // Tamper with ciphertext
            encOutput.data[0] = (encOutput.data[0] + 1).toByte()

            val decOutput = MutableByteArrayCursor(ByteArray(encOutput.cursor + 16))
            val result = encryption.decrypt(encOutput.data, 0, encOutput.cursor, nonce, aad, 0, aad.size, decOutput)
            assertFalse(result, "Decryption with tampered ciphertext should fail")
        }

        @Test
        fun `decrypt with wrong AAD fails`() {
            val key = createKey()
            val encryption = AeadEncryption(key)
            val plaintext = byteArrayOf(1, 2, 3, 4, 5)
            val nonce = createNonce12()
            val aad = createAad()

            val encOutput = MutableByteArrayCursor(ByteArray(plaintext.size + AeadEncryption.TAG_BYTES + 16))
            encryption.encrypt(plaintext, 0, plaintext.size, nonce, aad, 0, aad.size, encOutput)

            val wrongAad = byteArrayOf(0xFF.toByte(), 0x78, 0x00, 0x01)
            val decOutput = MutableByteArrayCursor(ByteArray(encOutput.cursor + 16))
            val result = encryption.decrypt(encOutput.data, 0, encOutput.cursor, nonce, wrongAad, 0, wrongAad.size, decOutput)
            assertFalse(result, "Decryption with wrong AAD should fail")
        }
    }

    @Nested
    inner class ExpandNonceTest {
        @Test
        fun `expandNonce correctly zero-pads 4-byte nonce to 12 bytes`() {
            val nonce4 = byteArrayOf(0x01, 0x02, 0x03, 0x04)
            val expanded = ByteArray(12)
            expandNonce(nonce4, expanded)

            assertEquals(0x01, expanded[0])
            assertEquals(0x02, expanded[1])
            assertEquals(0x03, expanded[2])
            assertEquals(0x04, expanded[3])
            for (i in 4 until 12) {
                assertEquals(0, expanded[i], "Byte at index $i should be zero")
            }
        }

        @Test
        fun `expandNonce overwrites existing data in expanded array`() {
            val nonce4 = byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(), 0xDD.toByte())
            val expanded = ByteArray(12) { 0xFF.toByte() }
            expandNonce(nonce4, expanded)

            assertEquals(0xAA.toByte(), expanded[0])
            assertEquals(0xBB.toByte(), expanded[1])
            assertEquals(0xCC.toByte(), expanded[2])
            assertEquals(0xDD.toByte(), expanded[3])
            for (i in 4 until 12) {
                assertEquals(0, expanded[i], "Byte at index $i should be zeroed out")
            }
        }

        @Test
        fun `expandNonce with all zeros`() {
            val nonce4 = ByteArray(4)
            val expanded = ByteArray(12)
            expandNonce(nonce4, expanded)

            for (i in 0 until 12) {
                assertEquals(0, expanded[i])
            }
        }
    }

    @Nested
    inner class Constants {
        @Test
        fun `NONCE_BYTES is 4`() {
            assertEquals(4, AeadEncryption.NONCE_BYTES)
        }

        @Test
        fun `EXPANDED_NONCE_BYTES is 12`() {
            assertEquals(12, AeadEncryption.EXPANDED_NONCE_BYTES)
        }

        @Test
        fun `TAG_BYTES is 16`() {
            assertEquals(16, AeadEncryption.TAG_BYTES)
        }

        @Test
        fun `TAG_BITS is 128`() {
            assertEquals(128, AeadEncryption.TAG_BITS)
        }
    }
}
