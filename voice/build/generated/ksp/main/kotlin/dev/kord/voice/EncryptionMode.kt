// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

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
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/voice-connections#establishing-a-voice-udp-connection-encryption-modes).
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

    final override fun toString(): String = "EncryptionMode.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [EncryptionMode].
     *
     * This is used as a fallback for [EncryptionMode]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: String,
    ) : EncryptionMode(value)

    public object XSalsa20Poly1305 : EncryptionMode("xsalsa20_poly1305")

    public object XSalsa20Poly1305Suffix : EncryptionMode("xsalsa20_poly1305_suffix")

    public object XSalsa20Poly1305Lite : EncryptionMode("xsalsa20_poly1305_lite")

    internal object Serializer : KSerializer<EncryptionMode> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.voice.EncryptionMode", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: EncryptionMode) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): EncryptionMode =
                when (val value = decoder.decodeString()) {
            "xsalsa20_poly1305" -> XSalsa20Poly1305
            "xsalsa20_poly1305_suffix" -> XSalsa20Poly1305Suffix
            "xsalsa20_poly1305_lite" -> XSalsa20Poly1305Lite
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [EncryptionMode]s.
         */
        public val entries: List<EncryptionMode> by lazy(mode = PUBLICATION) {
            listOf(
                XSalsa20Poly1305,
                XSalsa20Poly1305Suffix,
                XSalsa20Poly1305Lite,
            )
        }

    }
}
