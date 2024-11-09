package dev.kord.voice.udp

import com.iwebpp.crypto.*
import dev.kord.voice.EncryptionMode
import dev.kord.voice.XSalsa20_CONSTRUCTOR_DEPRECATION
import dev.kord.voice.XSalsa20_PROPERTY_DEPRECATION
import dev.kord.voice.encryption.AeadAes256GcmRtpSizeVoicePacketCreator
import dev.kord.voice.encryption.AeadXChaCha20Poly1305RtpSizeVoicePacketCreator
import dev.kord.voice.encryption.EncryptedVoicePacketCreator
import dev.kord.voice.encryption.*
import dev.kord.voice.encryption.strategies.*
import dev.kord.voice.io.ByteArrayView
import dev.kord.voice.io.MutableByteArrayCursor
import dev.kord.voice.io.mutableCursor
import dev.kord.voice.io.view

public abstract class AudioPacketProvider internal constructor(
    private val strategy: @Suppress("DEPRECATION") NonceStrategy?,
    public val key: ByteArray,
) {
    @Deprecated(
        "The 'nonceStrategy' property is only used for XSalsa20 Poly1305 encryption. Construct an " +
            "'AudioPacketProvider' instance without a 'nonceStrategy' instead. $XSalsa20_CONSTRUCTOR_DEPRECATION",
        ReplaceWith("AudioPacketProvider(key)", imports = ["dev.kord.voice.udp.AudioPacketProvider"]),
        DeprecationLevel.WARNING,
    )
    public constructor(key: ByteArray, nonceStrategy: @Suppress("DEPRECATION") NonceStrategy) : this(nonceStrategy, key)
    public constructor(key: ByteArray) : this(strategy = null, key)

    @Deprecated(
        "The 'nonceStrategy' property is only used for XSalsa20 Poly1305 encryption. An 'AudioPacketProvider' " +
            "instance can be created without a 'nonceStrategy' in which case this property throws an " +
            "'UnsupportedOperationException'. $XSalsa20_PROPERTY_DEPRECATION",
        level = DeprecationLevel.WARNING,
    )
    public val nonceStrategy: @Suppress("DEPRECATION") NonceStrategy
        get() = strategy ?: throw UnsupportedOperationException(
            "This AudioPacketProvider instance was created without a nonceStrategy."
        )

    public abstract fun provide(sequence: UShort, timestamp: UInt, ssrc: UInt, data: ByteArray): ByteArrayView
}

private class CouldNotEncryptDataException(data: ByteArray) :
    RuntimeException("Couldn't encrypt the following data: [${data.joinToString(", ")}]")

public class DefaultAudioPacketProvider private constructor(
    key: ByteArray, nonceStrategy: @Suppress("DEPRECATION") NonceStrategy?, private val delegate: ProviderDelegate,
) : AudioPacketProvider(nonceStrategy, key) {
    @Deprecated(
        "The 'nonceStrategy' property is only used for XSalsa20 Poly1305 encryption. Construct a " +
            "'DefaultAudioPacketProvider' instance with an 'EncryptionMode' instead of a 'nonceStrategy'. " +
            XSalsa20_CONSTRUCTOR_DEPRECATION,
        ReplaceWith(
            "DefaultAudioPacketProvider(key, EncryptionMode.AeadXChaCha20Poly1305RtpSize)",
            imports = ["dev.kord.voice.udp.DefaultAudioPacketProvider", "dev.kord.voice.EncryptionMode"],
        ),
        DeprecationLevel.WARNING,
    )
    public constructor(key: ByteArray, nonceStrategy: @Suppress("DEPRECATION") NonceStrategy) :
        this(key, nonceStrategy, LegacyProviderDelegate(key, nonceStrategy))

    internal constructor(
        key: ByteArray, nonceStrategy: @Suppress("DEPRECATION") NonceStrategy?, encryptionMode: EncryptionMode,
    ) : this(
        key, nonceStrategy,
        delegate = if (nonceStrategy != null) {
            LegacyProviderDelegate(key, nonceStrategy)
        } else @Suppress("DEPRECATION") when (encryptionMode) {
            EncryptionMode.AeadAes256GcmRtpSize ->
                EncryptedPacketCreatorProviderDelegate(AeadAes256GcmRtpSizeVoicePacketCreator(key))
            EncryptionMode.AeadXChaCha20Poly1305RtpSize ->
                EncryptedPacketCreatorProviderDelegate(AeadXChaCha20Poly1305RtpSizeVoicePacketCreator(key))
            EncryptionMode.XSalsa20Poly1305 -> LegacyProviderDelegate(key, NormalNonceStrategy())
            EncryptionMode.XSalsa20Poly1305Lite -> LegacyProviderDelegate(key, LiteNonceStrategy())
            EncryptionMode.XSalsa20Poly1305Suffix -> LegacyProviderDelegate(key, SuffixNonceStrategy())
            is EncryptionMode.Unknown -> throw UnsupportedOperationException("Unknown encryption mode $encryptionMode")
        },
    )

    public constructor(key: ByteArray, encryptionMode: EncryptionMode) : this(key, nonceStrategy = null, encryptionMode)

    override fun provide(sequence: UShort, timestamp: UInt, ssrc: UInt, data: ByteArray): ByteArrayView =
        delegate.provide(sequence, timestamp, ssrc, data)
}

private interface ProviderDelegate {
    fun provide(sequence: UShort, timestamp: UInt, ssrc: UInt, data: ByteArray): ByteArrayView
}

private class EncryptedPacketCreatorProviderDelegate(
    private val packetCreator: EncryptedVoicePacketCreator,
) : ProviderDelegate {
    private val lock = Any() // TODO do we need this lock?
    override fun provide(sequence: UShort, timestamp: UInt, ssrc: UInt, data: ByteArray): ByteArrayView =
        synchronized(lock) {
            packetCreator.createEncryptedVoicePacket(sequence, timestamp, ssrc, audioPlaintext = data).view()
        }
}

@Suppress("DEPRECATION")
private class LegacyProviderDelegate(key: ByteArray, private val nonceStrategy: NonceStrategy) : ProviderDelegate {

    private val codec = XSalsa20Poly1305Codec(key)

    private val packetBuffer = ByteArray(2048)
    private val packetBufferCursor: MutableByteArrayCursor = packetBuffer.mutableCursor()
    private val packetBufferView: ByteArrayView = packetBuffer.view()

    private val rtpHeaderView: ByteArrayView = packetBuffer.view(0, RTP_HEADER_LENGTH)!!

    private val nonceBuffer: MutableByteArrayCursor = ByteArray(TweetNaclFast.SecretBox.nonceLength).mutableCursor()

    private val lock: Any = Any()

    private fun MutableByteArrayCursor.writeHeader(sequence: Short, timestamp: Int, ssrc: Int) {
        writeByte(((2 shl 6) or (0x0) or (0x0)).toByte()) // first 2 bytes are version. the rest
        writeByte(PayloadType.Audio.raw)
        writeShort(sequence)
        writeInt(timestamp)
        writeInt(ssrc)
    }

    override fun provide(sequence: UShort, timestamp: UInt, ssrc: UInt, data: ByteArray): ByteArrayView =
        synchronized(lock) {
            with(packetBufferCursor) {
                this.reset()
                nonceBuffer.reset()

                // make sure we enough room in this buffer
                resize(RTP_HEADER_LENGTH + (data.size + TweetNaclFast.SecretBox.boxzerobytesLength) + nonceStrategy.nonceLength)

                // write header and generate nonce
                writeHeader(sequence.toShort(), timestamp.toInt(), ssrc.toInt())

                val rawNonce = nonceStrategy.generate { rtpHeaderView }
                nonceBuffer.writeByteView(rawNonce)

                // encrypt data and write into our buffer
                val encrypted = codec.encrypt(data, nonce = nonceBuffer.data, output = this)

                if (!encrypted) throw CouldNotEncryptDataException(data)

                nonceStrategy.append(rawNonce, this)

                // let's make sure we have the correct view of the packet
                if (!packetBufferView.resize(0, cursor)) error("couldn't resize packet buffer view?!")

                packetBufferView
            }
        }
}
