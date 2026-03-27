package dev.kord.core.event.guild

import dev.kord.core.Kord
import dev.kord.core.entity.StageInstance
import dev.kord.core.event.Event

public class StageInstanceDeleteEvent(
    public val stageInstance: StageInstance,
    override val shard: Int,
    override val customContext: Any?,
    override val kord: Kord
) : Event {
    override fun toString(): String {
        return "StageInstanceDeleteEvent(stageInstance=$stageInstance, shard=$shard)"
    }
}