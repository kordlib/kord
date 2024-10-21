package dev.kord.voice.udp

import dev.kord.common.annotation.KordVoice

@KordVoice
public actual data class SocketAddress actual constructor(
    public actual val hostname: String,
    public actual val port: Int,
)
