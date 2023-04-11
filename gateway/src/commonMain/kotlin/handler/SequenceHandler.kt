package dev.kord.gateway.handler

import dev.kord.gateway.Close
import dev.kord.gateway.DispatchEvent
import dev.kord.gateway.Event
import dev.kord.gateway.Sequence
import kotlinx.coroutines.flow.Flow

internal class SequenceHandler(
    flow: Flow<Event>,
    private val sequence: Sequence
) : Handler(flow, "SequenceHandler") {

    init {
        on<DispatchEvent> { event ->
            sequence.value = event.sequence ?: sequence.value
        }

        on<Close.SessionReset> {
            sequence.value = null
        }
    }

}