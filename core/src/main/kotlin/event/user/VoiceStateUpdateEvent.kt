package dev.kord.core.event.user

import dev.kord.core.Kord
import dev.kord.core.entity.VoiceState
import dev.kord.core.event.Event
import kotlin.coroutines.CoroutineContext

class VoiceStateUpdateEvent(
    val old: VoiceState?,
    val state: VoiceState,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = state.kord.coroutineContext,
) : Event {
    override val kord: Kord get() = state.kord

    override fun toString(): String {
        return "VoiceStateUpdateEvent(old=$old, state=$state, shard=$shard)"
    }
}