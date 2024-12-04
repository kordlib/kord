@file:Generate(
    STRING_KORD_ENUM, name = "EncryptionMode",
    docUrl = "https://discord.com/developers/docs/topics/voice-connections#establishing-a-voice-udp-connection-encryption-modes",
    entries = [
        Entry("AeadAes256Gcm", stringValue = "aead_aes256_gcm"),
        Entry("AeadAes256GcmRtpSize", stringValue = "aead_aes256_gcm_rtpsize"),
        Entry("XSalsa20Poly1305", stringValue = "xsalsa20_poly1305"),
        Entry("XSalsa20Poly1305Suffix", stringValue = "xsalsa20_poly1305_suffix"),
        Entry("XSalsa20Poly1305Lite", stringValue = "xsalsa20_poly1305_lite"),
    ]
)

package dev.kord.voice

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.STRING_KORD_ENUM
import dev.kord.ksp.Generate.Entry
