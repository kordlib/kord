package dev.kord.gateway.builder

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.PresenceStatus
import dev.kord.gateway.DiscordPresence
import dev.kord.gateway.Intents
import dev.kord.gateway.nonPrivileged
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class Shards(val totalShards: Int, val indices: Iterable<Int> = 0 until totalShards)

@KordDsl
public class LoginBuilder {
    public var presence: DiscordPresence = DiscordPresence(PresenceStatus.Online, false)
    public var intents: Intents = Intents.nonPrivileged
    public var name: String = "Kord"

    public inline fun presence(builder: PresenceBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        this.presence = PresenceBuilder().apply(builder).toPresence()
    }

    public inline fun intents(builder: Intents.Builder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        this.intents = Intents(builder)
    }
}
