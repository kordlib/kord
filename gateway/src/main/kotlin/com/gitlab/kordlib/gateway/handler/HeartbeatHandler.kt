package com.gitlab.kordlib.gateway.handler

import com.gitlab.kordlib.gateway.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
internal class HeartbeatHandler(
        flow: Flow<Event>,
        private val send: suspend (Command) -> Unit,
        private val restart: suspend () -> Unit,
        private val sequence: Sequence,
        private val ticker: Ticker = Ticker()
) : Handler(flow) {

    private val possibleZombie = atomic(false)

    override fun start() {
        on<Event> {
            possibleZombie.update { false }
        }

        on<Hello> { hello ->
            ticker.tickAt(hello.heartbeatInterval) {
                if (possibleZombie.value) {
                    restart()
                } else {
                    possibleZombie.update { true }
                    send(Command.Heartbeat(sequence.value))
                }
            }
        }

        on<Heartbeat> {
            send(Command.Heartbeat(sequence.value))
        }

        on<Close> {
            ticker.stop()
        }
    }
}