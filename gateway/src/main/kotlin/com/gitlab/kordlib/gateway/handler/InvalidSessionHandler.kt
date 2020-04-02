package com.gitlab.kordlib.gateway.handler

import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.flow.Flow

internal class InvalidSessionHandler(
        flow: Flow<Event>,
        private val restart: suspend (event: Close) -> Unit
) : Handler(flow, "InvalidSessionHandler") {

    override fun start() {
        on<InvalidSession> {
            if (it.resumable) restart(Close.Reconnecting)
            else restart(Close.SessionReset)
        }
    }

}