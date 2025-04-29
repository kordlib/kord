// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.voice

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * See [EncryptionMode]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/voice-connections#transport-encryption-modes).
 */
@Serializable(with = EncryptionMode.Serializer::class)
public sealed class EncryptionMode(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is EncryptionMode && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "EncryptionMode.Unknown(value=$value)"
            else "EncryptionMode.${this::class.simpleName}"

    /**
     * An unknown [EncryptionMode].
     *
     * This is used as a fallback for [EncryptionMode]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: String,
    ) : EncryptionMode(value)

    public object AeadAes256GcmRtpSize : EncryptionMode("aead_aes256_gcm_rtpsize")

    public object AeadXChaCha20Poly1305RtpSize : EncryptionMode("aead_xchacha20_poly1305_rtpsize")

    @Deprecated(
        message =
                "Use 'EncryptionMode.from(\"xsalsa20_poly1305\")' if you need to keep using this deprecated 'EncryptionMode'. XSalsa20 Poly1305 encryption is deprecated for Discord voice connections and will be discontinued as of November 18th, 2024. As of this date, the voice gateway will not allow you to connect with one of the deprecated encryption modes. See https://discord.com/developers/docs/change-log#voice-encryption-modes for details. The deprecation level will be raised to ERROR in 0.17.0, to HIDDEN in 0.18.0, and this object will be removed in 0.19.0.",
        replaceWith = ReplaceWith(expression = "EncryptionMode.from(\"xsalsa20_poly1305\")", imports
                    = arrayOf("dev.kord.voice.EncryptionMode")),
    )
    public object XSalsa20Poly1305 : EncryptionMode("xsalsa20_poly1305")

    @Deprecated(
        message =
                "Use 'EncryptionMode.from(\"xsalsa20_poly1305_suffix\")' if you need to keep using this deprecated 'EncryptionMode'. XSalsa20 Poly1305 encryption is deprecated for Discord voice connections and will be discontinued as of November 18th, 2024. As of this date, the voice gateway will not allow you to connect with one of the deprecated encryption modes. See https://discord.com/developers/docs/change-log#voice-encryption-modes for details. The deprecation level will be raised to ERROR in 0.17.0, to HIDDEN in 0.18.0, and this object will be removed in 0.19.0.",
        replaceWith = ReplaceWith(expression = "EncryptionMode.from(\"xsalsa20_poly1305_suffix\")",
                    imports = arrayOf("dev.kord.voice.EncryptionMode")),
    )
    public object XSalsa20Poly1305Suffix : EncryptionMode("xsalsa20_poly1305_suffix")

    @Deprecated(
        message =
                "Use 'EncryptionMode.from(\"xsalsa20_poly1305_lite\")' if you need to keep using this deprecated 'EncryptionMode'. XSalsa20 Poly1305 encryption is deprecated for Discord voice connections and will be discontinued as of November 18th, 2024. As of this date, the voice gateway will not allow you to connect with one of the deprecated encryption modes. See https://discord.com/developers/docs/change-log#voice-encryption-modes for details. The deprecation level will be raised to ERROR in 0.17.0, to HIDDEN in 0.18.0, and this object will be removed in 0.19.0.",
        replaceWith = ReplaceWith(expression = "EncryptionMode.from(\"xsalsa20_poly1305_lite\")",
                    imports = arrayOf("dev.kord.voice.EncryptionMode")),
    )
    public object XSalsa20Poly1305Lite : EncryptionMode("xsalsa20_poly1305_lite")

    internal object Serializer : KSerializer<EncryptionMode> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.voice.EncryptionMode", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: EncryptionMode) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): EncryptionMode = from(decoder.decodeString())
    }

    public companion object {
        /**
         * A [List] of all known [EncryptionMode]s.
         */
        public val entries: List<EncryptionMode> by lazy(mode = PUBLICATION) {
            listOf(
                AeadAes256GcmRtpSize,
                AeadXChaCha20Poly1305RtpSize,
                @Suppress("DEPRECATION") XSalsa20Poly1305,
                @Suppress("DEPRECATION") XSalsa20Poly1305Suffix,
                @Suppress("DEPRECATION") XSalsa20Poly1305Lite,
            )
        }

        /**
         * Returns an instance of [EncryptionMode] with [EncryptionMode.value] equal to the
         * specified [value].
         */
        public fun from(`value`: String): EncryptionMode = when (value) {
            "aead_aes256_gcm_rtpsize" -> AeadAes256GcmRtpSize
            "aead_xchacha20_poly1305_rtpsize" -> AeadXChaCha20Poly1305RtpSize
            "xsalsa20_poly1305" -> @Suppress("DEPRECATION") XSalsa20Poly1305
            "xsalsa20_poly1305_suffix" -> @Suppress("DEPRECATION") XSalsa20Poly1305Suffix
            "xsalsa20_poly1305_lite" -> @Suppress("DEPRECATION") XSalsa20Poly1305Lite
            else -> Unknown(value)
        }
    }
}
