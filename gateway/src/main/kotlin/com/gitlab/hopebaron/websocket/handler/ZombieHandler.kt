package com.gitlab.hopebaron.websocket.handler

import com.gitlab.hopebaron.websocket.Event
import com.gitlab.hopebaron.websocket.HeartbeatACK
import com.gitlab.hopebaron.websocket.Hello
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@FlowPreview
internal class ZombieHandler(flow: Flow<Event>, private val restart: suspend () -> Unit) : Handler(flow) {

    private val possibleZombie = atomic(false)
    private val delay = atomic(Long.MAX_VALUE)

    override fun start() {
        on<Hello> { event ->
            delay.update { event.heartbeatInterval }
        }

        on<Event> {
            if (it is HeartbeatACK) return@on
            possibleZombie.update { false }
        }

        on<HeartbeatACK> {
            possibleZombie.update { true }
            launch {
                delay(delay.value)
                if (possibleZombie.value) restart()
            }
        }
    }

}