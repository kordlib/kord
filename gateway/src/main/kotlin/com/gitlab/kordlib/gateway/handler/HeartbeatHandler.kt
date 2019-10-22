package com.gitlab.kordlib.gateway.handler

import com.gitlab.kordlib.gateway.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlin.time.ClockMark
import kotlin.time.Duration
import kotlin.time.MonoClock

@ObsoleteCoroutinesApi
internal class HeartbeatHandler(
        flow: Flow<Event>,
        private val send: suspend (Command) -> Unit,
        private val restart: suspend () -> Unit,
        private val ping: (Duration) -> Unit,
        private val sequence: Sequence,
        private val ticker: Ticker = Ticker()
) : Handler(flow) {

    private val possibleZombie = atomic(false)
    private var timestamp: ClockMark = MonoClock.markNow()

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
                    timestamp = MonoClock.markNow()
                    send(Command.Heartbeat(sequence.value))
                }
            }
        }

        on<Heartbeat> {
            timestamp = MonoClock.markNow()
            send(Command.Heartbeat(sequence.value))
        }

        on<HeartbeatACK> {
            ping(timestamp.elapsedNow())
        }

        on<Close> {
            ticker.stop()
        }
    }
}