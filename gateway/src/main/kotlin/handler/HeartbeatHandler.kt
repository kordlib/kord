package dev.kord.gateway.handler

import dev.kord.gateway.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@ObsoleteCoroutinesApi
internal class HeartbeatHandler(
    flow: Flow<Event>,
    private val send: suspend (Command) -> Unit,
    private val restart: suspend () -> Unit,
    private val ping: (Duration) -> Unit,
    private val sequence: Sequence,
    private val ticker: Ticker = Ticker(),
    private val timeSource: TimeSource = TimeSource.Monotonic,
) : Handler(flow, "HeartbeatHandler") {

    private val possibleZombie = atomic(false)
    private var timestamp: TimeMark = timeSource.markNow()

    private fun noZombie() = possibleZombie.update { false }
    private fun maybeZombie() = possibleZombie.update { true }

    override fun start() {
        on<Event> {
            noZombie()
        }

        on<Hello> { (heartbeatInterval) ->
            ticker.tickAt(heartbeatInterval.toLong()) {
                if (possibleZombie.value) {
                    restart()
                } else {
                    maybeZombie()
                    timestamp = timeSource.markNow()
                    send(Command.Heartbeat(sequence.value))
                }
            }

            timestamp = TimeSource.Monotonic.markNow()
            send(Command.Heartbeat(sequence.value))
        }

        on<Heartbeat> {
            timestamp = TimeSource.Monotonic.markNow()
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