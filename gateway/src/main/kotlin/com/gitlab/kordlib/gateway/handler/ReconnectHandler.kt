package com.gitlab.kordlib.gateway.handler

import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.Reconnect
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

internal class ReconnectHandler(
        flow: Flow<Event>,
        private val reconnect: suspend () -> Unit
) : Handler(flow, "ReconnectHandler") {

    override fun start() {
        on<Reconnect> {
            reconnect()
        }
    }
}