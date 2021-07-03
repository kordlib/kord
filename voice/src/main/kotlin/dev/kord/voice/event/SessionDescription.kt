package dev.kord.voice.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionDescription(
    val mode: String,
    @SerialName("secret_key")
    val secretKey: List<Int>
)
