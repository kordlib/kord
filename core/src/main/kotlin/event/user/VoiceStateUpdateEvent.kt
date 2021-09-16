package dev.kord.core.event.user

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.VoiceState
import dev.kord.core.event.Event

class VoiceStateUpdateEvent(
    val old: VoiceState?,
    val state: VoiceState,
    override val shard: Int
) : Event {
    override val kord: Kord get() = state.kord
    override val guildId: Snowflake?
        get() = state.guildId

    override fun toString(): String {
        return "VoiceStateUpdateEvent(old=$old, state=$state, shard=$shard)"
    }
}