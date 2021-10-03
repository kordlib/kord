package dev.kord.gateway.builder

import dev.kord.common.entity.PresenceStatus
import dev.kord.gateway.Intents
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

data class Shards(val totalShards: Int, val indices: Iterable<Int> = 0 until totalShards)

class LoginBuilder {
    var presence: PresenceBuilder.() -> Unit = { status = PresenceStatus.Online }
    var intents: Intents = Intents.nonPrivileged
    var name: String = "Kord"

    @OptIn(ExperimentalContracts::class)
    fun presence(builder: PresenceBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        this.presence = builder
    }
}
