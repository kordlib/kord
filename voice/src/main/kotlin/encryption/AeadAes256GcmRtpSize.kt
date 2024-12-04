package dev.kord.voice.encryption

import dev.kord.voice.udp.DecryptedVoicePacket
import dev.kord.voice.udp.RTP_HEADER_LENGTH
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.UnsafeIoApi
import kotlinx.io.readTo
import kotlinx.io.unsafe.UnsafeBufferOperations
import java.security.Security
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val AES_256_GCM_NO_PADDING = "AES_256/GCM/NoPadding"
private const val AES = "AES"

internal val isAes256GcmSupported
    get() = Security.getAlgorithms("Cipher")
        .any { algorithm -> AES_256_GCM_NO_PADDING.equals(algorithm, ignoreCase = true) }

private const val AUTH_TAG_SIZE = 16
private const val AUTH_TAG_BITS = AUTH_TAG_SIZE * 8
private const val IV_SIZE = 12
private const val NONCE_SIZE = 4
private const val ADDITIONAL_SIZE = RTP_HEADER_LENGTH + AUTH_TAG_SIZE + NONCE_SIZE

internal class AeadAes256GcmRtpSizeVoicePacketCreator(key: ByteArray) : EncryptedVoicePacketCreator {

    // the first 4 bytes are the 32-bit incremental nonce (big endian), the remaining bytes are 0
    private val ivBuffer = ByteArray(IV_SIZE)
    private var nonce = 0
    private val cipher: Cipher = Cipher.getInstance(AES_256_GCM_NO_PADDING)
    private val key = SecretKeySpec(key, AES)

    override fun createEncryptedVoicePacket(
        sequence: UShort,
        timestamp: UInt,
        ssrc: UInt,
        audioPlaintext: ByteArray, // TODO rename to plaintextAudio?
    ): ByteArray {
        val nonce = nonce++
        val plaintextSize = audioPlaintext.size
        val packetSize = plaintextSize + ADDITIONAL_SIZE
        val packet = ByteArray(packetSize) // TODO use cipher.getOutputSize?

        // write the header into the voice packet
        packet.writeRtpHeader(sequence, timestamp, ssrc)

        // encrypt the audio into the voice packet
        ivBuffer.writeIntBigEndian(offset = 0, nonce)
        cipher.init(ENCRYPT_MODE, key, GCMParameterSpec(AUTH_TAG_BITS, ivBuffer))
        cipher.updateAAD(packet, /* offset = */ 0, /* len = */ RTP_HEADER_LENGTH)
        val written = cipher.doFinal(
            /* input = */ audioPlaintext, /* inputOffset = */ 0, /* inputLen = */ plaintextSize,
            /* output = */ packet, /* outputOffset = */ RTP_HEADER_LENGTH,
        )
        check(written == plaintextSize + AUTH_TAG_SIZE) { "Ciphertext doesn't have the expected length." }

        // append the nonce to the end of the voice packet
        packet.writeIntBigEndian(offset = packetSize - NONCE_SIZE, nonce)

        return packet
    }
}

internal class AeadAes256GcmRtpSizeVoicePacketDecryptor(key: ByteArray) : Decrypt() {
    private val ivBuffer = ByteArray(IV_SIZE)
    private val cipher: Cipher = Cipher.getInstance(AES_256_GCM_NO_PADDING)
    private val key = SecretKeySpec(key, AES)

    override fun decrypt(audioPacket: Source): DecryptedVoicePacket? = audioPacket.use { packet ->
        val headerSize = readUnencryptedRtpHeaderPart(packet)
        if (headerSize < 0) {
            return null
        }
        val input = Buffer()
        var output: Buffer? = null
        try {
            val payloadSize = packet.transferTo(input)
            // TODO padding handling
            if (payloadSize < NONCE_SIZE + AUTH_TAG_SIZE) {
                return null
            }

            output = Buffer()

            // read the nonce from the end of the voice packet
            // TODO copy directly to ivBuffer when https://github.com/Kotlin/kotlinx-io/issues/191 is implemented
            input.copyTo(output, startIndex = payloadSize - NONCE_SIZE)
            output.readTo(ivBuffer, startIndex = 0, endIndex = NONCE_SIZE)

            cipher.init(DECRYPT_MODE, key, GCMParameterSpec(AUTH_TAG_BITS, ivBuffer))
            cipher.updateAAD(unencryptedRtpHeaderPartBuffer, /* offset = */ 0, /* len = */ headerSize)
            return if (input.decryptTo(output)) {
                createDecryptedVoicePacket(headerSize, output)
            } else {
                null
            }
        } catch (e: Throwable) {
            output?.clear()
            throw e
        } finally {
            input.clear() // release buffer segments
        }
    }

    @OptIn(UnsafeIoApi::class)
    private fun Buffer.decryptTo(output: Buffer) = try {
        var sizeWithoutNonce = size - NONCE_SIZE
        while (sizeWithoutNonce > 0) {
            sizeWithoutNonce -= decryptPartTo(output, maxInputLen = sizeWithoutNonce)
        }

        UnsafeBufferOperations.writeToTail(
            output,
            minimumCapacity = maxOf(1, cipher.getOutputSize(/* inputLen = */ 0))
        ) { outputBytes, outputStartIndex, _ ->
            return@writeToTail cipher.doFinal(outputBytes, outputStartIndex)
        }
        true
    } catch (_: AEADBadTagException) {
        false
    }

    /**
     * [Updates][Cipher.update] the [cipher] with up to [maxInputLen] bytes from this [Buffer] into [output] and returns
     * the number of bytes consumed from this [Buffer].
     */
    @UnsafeIoApi
    private fun Buffer.decryptPartTo(output: Buffer, maxInputLen: Long): Int =
        UnsafeBufferOperations.readFromHead(buffer = this) { inputBytes, inputStartIndex, inputEndIndex ->
            val inputLen = minOf((inputEndIndex - inputStartIndex).toLong(), maxInputLen).toInt()

            UnsafeBufferOperations.writeToTail(
                buffer = output,
                minimumCapacity = cipher.getOutputSize(inputLen),
            ) { outputBytes, outputStartIndex, _ ->
                return@writeToTail cipher.update(inputBytes, inputStartIndex, inputLen, outputBytes, outputStartIndex)
            }

            return@readFromHead inputLen // will be consumed from buffer
        }
}
