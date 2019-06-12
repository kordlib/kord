package com.gitlab.hopebaron.websocket.handler

import com.gitlab.hopebaron.websocket.DispatchEvent
import com.gitlab.hopebaron.websocket.Event
import com.gitlab.hopebaron.websocket.Sequence
import com.gitlab.hopebaron.websocket.SessionClose
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
internal class SequenceHandler(
        flow: Flow<Event>,
        private val sequence: Sequence
) : Handler(flow) {

    init {
        on<DispatchEvent> { event ->
            sequence.value = event.sequence ?: sequence.value
        }

        on<SessionClose> {
            sequence.value = null
        }
    }

}