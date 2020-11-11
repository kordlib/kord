package com.gitlab.kordlib.core.event.user

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.VoiceState
import com.gitlab.kordlib.core.event.Event

class VoiceStateUpdateEvent(
        val old: VoiceState?,
        val state: VoiceState,
        override val shard: Int
) : Event {
    override val kord: Kord get() = state.kord

    override fun toString(): String {
        return "VoiceStateUpdateEvent(old=$old, state=$state, shard=$shard)"
    }
}