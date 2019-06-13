package com.gitlab.hopebaron.websocket.handler

import com.gitlab.hopebaron.websocket.Event
import com.gitlab.hopebaron.websocket.Reconnect
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
internal class ReconnectHandler(
        flow: Flow<Event>,
        private val reconnect: suspend () -> Unit
) : Handler(flow) {

    override fun start() {
        on<Reconnect> {
            reconnect
        }
    }
}