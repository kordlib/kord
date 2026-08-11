package dev.kord.core.compilation.patternMatching

import dev.kord.core.entity.channel.thread.DeletedThreadChannel
import dev.kord.core.entity.channel.thread.MaybeThreadChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.channel.ChannelsChangeEvent
import dev.kord.core.event.channel.thread.*

fun maybeThreadChannel(it: MaybeThreadChannel): Int {
    return when (it) {
        is DeletedThreadChannel -> 1
        is ThreadChannel -> 2
    }
}

fun channelsChangeEvent(it: ChannelsChangeEvent): Int {
    return when (it) {
        is ChannelCreateEvent -> 1
        is ChannelDeleteEvent -> 2
        is ChannelUpdateEvent -> 3
    }
}

fun threadsChangeEvent(it: ThreadsChangeEvent): Int {
    return when (it) {
        is ThreadChannelCreateEvent -> when (it) {
            is NewsChannelThreadCreateEvent -> 1
            is TextChannelThreadCreateEvent -> 2
            is UnknownChannelThreadCreateEvent -> 3
        }

        is ThreadChannelDeleteEvent -> when (it) {
            is NewsChannelThreadDeleteEvent -> 4
            is TextChannelThreadDeleteEvent -> 5
            is UnknownChannelThreadDeleteEvent -> 6
        }
    }
}

fun threadsChangeEvent2(it: ThreadsChangeEvent): Int {
    return when (it) {
        is NewsChannelThreadCreateEvent -> 1
        is TextChannelThreadCreateEvent -> 2
        is UnknownChannelThreadCreateEvent -> 3
        is NewsChannelThreadDeleteEvent -> 4
        is TextChannelThreadDeleteEvent -> 5
        is UnknownChannelThreadDeleteEvent -> 6
    }
}