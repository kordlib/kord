package dev.kord.gateway.handler

import dev.kord.gateway.Event
import dev.kord.gateway.Reconnect
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