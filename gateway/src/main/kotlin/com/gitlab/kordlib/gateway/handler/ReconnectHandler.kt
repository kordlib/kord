package com.gitlab.kordlib.gateway.handler

import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.Reconnect
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
internal class ReconnectHandler(
        flow: Flow<Event>,
        private val reconnect: suspend () -> Unit
) : Handler(flow) {

    override fun start() {
        on<Reconnect> {
            reconnect()
        }
    }
}