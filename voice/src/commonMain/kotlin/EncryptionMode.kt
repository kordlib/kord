@file:GenerateKordEnum(
    name = "EncryptionMode", valueType = STRING,
    docUrl = "https://discord.com/developers/docs/topics/voice-connections#establishing-a-voice-udp-connection-encryption-modes",
    entries = [
        Entry("XSalsa20Poly1305", stringValue = "xsalsa20_poly1305"),
        Entry("XSalsa20Poly1305Suffix", stringValue = "xsalsa20_poly1305_suffix"),
        Entry("XSalsa20Poly1305Lite", stringValue = "xsalsa20_poly1305_lite")
    ]
)

package dev.kord.voice

import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.STRING