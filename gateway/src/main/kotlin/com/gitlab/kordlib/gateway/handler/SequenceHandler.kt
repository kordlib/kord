package com.gitlab.kordlib.gateway.handler

import com.gitlab.kordlib.gateway.Close
import com.gitlab.kordlib.gateway.DispatchEvent
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.Sequence
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