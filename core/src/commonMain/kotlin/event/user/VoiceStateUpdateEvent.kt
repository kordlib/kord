package dev.kord.core.event.user

import dev.kord.core.Kord
import dev.kord.core.entity.VoiceState
import dev.kord.core.event.Event

/**
 * The event dispatched when a [VoiceState] is updated.
 *
 * See [Voice State update](https://discord.com/developers/docs/topics/gateway-events#voice-state-update)
 *
 * @param old The old [VoiceState] that triggered the event. It may be `null` if it was not stored in the cache
 * @param state The [VoiceState] that triggered the event.
 */
public class VoiceStateUpdateEvent(
    public val old: VoiceState?,
    public val state: VoiceState,
    override val shard: Int,
    override val customContext: Any?,
) : Event {
    override val kord: Kord get() = state.kord

    override fun toString(): String {
        return "VoiceStateUpdateEvent(old=$old, state=$state, shard=$shard)"
    }
}
