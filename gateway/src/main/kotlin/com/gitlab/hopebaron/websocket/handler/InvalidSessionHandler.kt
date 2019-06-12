package com.gitlab.hopebaron.websocket.handler

import com.gitlab.hopebaron.websocket.*
import kotlinx.coroutines.flow.Flow

internal class InvalidSessionHandler(
        flow: Flow<Event>,
        private val restart: suspend (event: Close) -> Unit
)  : Handler(flow) {

    override fun start() {
        on<InvalidSession> {
            if(it.resumable)  restart(CloseForReconnect)
            else restart(SessionClose)
        }
    }

}