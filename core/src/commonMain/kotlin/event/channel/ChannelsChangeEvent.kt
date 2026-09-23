package dev.kord.core.event.channel

import dev.kord.core.Kord
import dev.kord.core.entity.channel.Channel
import dev.kord.core.event.Event

public sealed interface ChannelsChangeEvent: Event {
    public val channel: Channel
    override val kord: Kord
        get() = channel.kord
}