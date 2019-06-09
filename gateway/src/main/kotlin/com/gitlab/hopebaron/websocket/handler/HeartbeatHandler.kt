package com.gitlab.hopebaron.websocket.handler

import com.gitlab.hopebaron.websocket.*
import com.gitlab.hopebaron.websocket.Command.Heartbeat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
internal class HeartbeatHandler(
        flow: Flow<Event>,
        private val send: suspend (Command) -> Unit,
        private val sequence: Sequence,
        private val ticker: Ticker = Ticker()
) : Handler(flow) {

    override fun start() {
        on<Hello> {
            ticker.tickAt(it.heartbeatInterval) { send(Heartbeat(sequence.value)) }
        }

        on<com.gitlab.hopebaron.websocket.Heartbeat> {
            send(Heartbeat(sequence.value))
        }

        on<Close> {
            ticker.stop()
        }
    }
}