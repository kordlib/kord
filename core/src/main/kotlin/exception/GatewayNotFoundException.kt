package dev.kord.core.exception

import dev.kord.common.entity.Snowflake

public class GatewayNotFoundException : Exception {
    public constructor(message: String) : super(message)
    public constructor(cause: Throwable) : super(cause)
    public constructor(message: String, cause: Throwable) : super(message, cause)

    @Suppress("NOTHING_TO_INLINE")
    public companion object {
        public inline fun voiceConnectionGatewayNotFound(guildId: Snowflake): Nothing =
            throw GatewayNotFoundException("Wasn't able to find a gateway for the guild with id $guildId while creating a VoiceConnection!")
    }
}
