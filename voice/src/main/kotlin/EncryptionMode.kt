package dev.kord.voice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EncryptionMode {
    @SerialName("xsalsa20_poly1305")
    XSalsa20Poly1305,

    @SerialName("xsalsa20_poly1305_suffix")
    XSalsa20Poly1305Suffix,

    @SerialName("xsalsa20_poly1305_lite")
    XSalsa20Poly1306Lite,

    @SerialName("xsalsa20_poly1305_lite_rtpsize")
    XSalsa20Poly1305LiteRtpsize,

    // video stuff... unused. though required to allow for serialization of ready
    @SerialName("aead_aes256_gcm_rtpsize")
    AeadAes256GcmRtpsize,

    @SerialName("aead_aes256_gcm")
    AeadAes256Gcm,
}