package dev.kord.gateway.builder

import dev.kord.common.entity.PresenceStatus
import dev.kord.gateway.DiscordPresence
import dev.kord.gateway.Intents
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

data class Shards(val totalShards: Int, val indices: Iterable<Int> = 0 until totalShards)

class LoginBuilder {
    var presence: DiscordPresence = DiscordPresence(PresenceStatus.Online, false)
    var intents: Intents = Intents.nonPrivileged
    var name: String = "Kord"

    fun presence(builder: PresenceBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        this.presence = PresenceBuilder().apply(builder).toPresence()
    }
}
