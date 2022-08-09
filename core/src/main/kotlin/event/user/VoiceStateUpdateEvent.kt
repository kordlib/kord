package dev.kord.core.event.user

import dev.kord.core.Kord
import dev.kord.core.entity.VoiceState
import dev.kord.core.event.Event

public class VoiceStateUpdateEvent(
    public val old: VoiceState?,
    public val state: VoiceState,
    override val shard: Int,
) : Event {
    override val kord: Kord get() = state.kord

    override fun toString(): String {
        return "VoiceStateUpdateEvent(old=$old, state=$state, shard=$shard)"
    }
}
