package dev.kord.core.event.channel.thread

import dev.kord.core.entity.channel.thread.MaybeThreadChannel
import dev.kord.core.event.Event

public sealed interface ThreadsChangeEvent: Event {
    public val channel: MaybeThreadChannel
    public override val shard: Int
    public override val customContext: Any?
}