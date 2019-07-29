package com.gitlab.kordlib.gateway.handler

import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
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