package dev.kord.core.compilation.patternMatching

import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.channel.thread.ThreadChannelCreateEvent
import dev.kord.core.event.channel.thread.ThreadChannelDeleteEvent

fun eventMatching(event: Event): Int {
    return when (event) {
        is ChannelCreateEvent -> 1
        is ChannelDeleteEvent -> 2
        is ChannelUpdateEvent -> 3
        is ThreadChannelDeleteEvent -> 4
        is ThreadChannelCreateEvent -> 5
        else -> 0
    }
}