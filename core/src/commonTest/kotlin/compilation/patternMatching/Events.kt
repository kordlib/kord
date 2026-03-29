package dev.kord.core.compilation.patternMatching

import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.channel.thread.ThreadChannelCreateEvent
import dev.kord.core.event.channel.thread.ThreadChannelDeleteEvent
import dev.kord.core.event.message.*

fun eventMatching(event: Event): Int {
    return when (event) {
        is ChannelCreateEvent -> 1
        is ChannelDeleteEvent -> 2
        is ChannelUpdateEvent -> 3
        is ThreadChannelDeleteEvent -> 4
        is ThreadChannelCreateEvent -> 5
        is MessageCreateEvent -> 6
        is MessageDeleteEvent -> 7
        is MessageBulkDeleteEvent -> 8
        is MessageUpdateEvent -> 9
        else -> 0
    }
}

fun messagesChangeEventMatching(event: MessagesChangeEvent): Int {
    return when (event) {
        is MessageChangeEvent -> when (event) {
            is MessageCreateEvent -> 1
            is MessageDeleteEvent -> 2
            is MessageUpdateEvent -> 3
        }
        is MessageBulkDeleteEvent -> 3
        else -> 0
    }
}