package com.gitlab.kordlib.gateway.handler

import com.gitlab.kordlib.gateway.DispatchEvent
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.Sequence
import com.gitlab.kordlib.gateway.SessionClose
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow

internal class SequenceHandler(
        flow: Flow<Event>,
        private val sequence: Sequence
) : Handler(flow, "SequenceHandler") {

    init {
        on<DispatchEvent> { event ->
            sequence.value = event.sequence ?: sequence.value
        }

        on<SessionClose> {
            sequence.value = null
        }
    }

}