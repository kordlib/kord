@file:Generate(
    STRING_KORD_ENUM, name = "EncryptionMode",
    docUrl = "https://discord.com/developers/docs/topics/voice-connections#transport-encryption-modes",
    entries = [
        Entry("AeadAes256GcmRtpSize", stringValue = "aead_aes256_gcm_rtpsize"),
        Entry("AeadXChaCha20Poly1305RtpSize", stringValue = "aead_xchacha20_poly1305_rtpsize"),
        Entry(
            "XSalsa20Poly1305", stringValue = "xsalsa20_poly1305",
            deprecated = Deprecated(
                """Use 'EncryptionMode.from("xsalsa20_poly1305")' if you need to keep using this deprecated """ +
                    "'EncryptionMode'. $XSalsa20_OBJECT_DEPRECATION",
                ReplaceWith(
                    """EncryptionMode.from("xsalsa20_poly1305")""", imports = ["dev.kord.voice.EncryptionMode"],
                ),
                DeprecationLevel.WARNING,
            ),
        ),
        Entry(
            "XSalsa20Poly1305Suffix", stringValue = "xsalsa20_poly1305_suffix",
            deprecated = Deprecated(
                """Use 'EncryptionMode.from("xsalsa20_poly1305_suffix")' if you need to keep using this deprecated """ +
                    "'EncryptionMode'. $XSalsa20_OBJECT_DEPRECATION",
                ReplaceWith(
                    """EncryptionMode.from("xsalsa20_poly1305_suffix")""", imports = ["dev.kord.voice.EncryptionMode"],
                ),
                DeprecationLevel.WARNING,
            ),
        ),
        Entry(
            "XSalsa20Poly1305Lite", stringValue = "xsalsa20_poly1305_lite",
            deprecated = Deprecated(
                """Use 'EncryptionMode.from("xsalsa20_poly1305_lite")' if you need to keep using this deprecated """ +
                    "'EncryptionMode'. $XSalsa20_OBJECT_DEPRECATION",
                ReplaceWith(
                    """EncryptionMode.from("xsalsa20_poly1305_lite")""", imports = ["dev.kord.voice.EncryptionMode"],
                ),
                DeprecationLevel.WARNING,
            ),
        ),
    ]
)

package dev.kord.voice

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.STRING_KORD_ENUM
import dev.kord.ksp.Generate.Entry
