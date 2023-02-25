package dev.kord.gateway.handler

import dev.kord.gateway.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@OptIn(ObsoleteCoroutinesApi::class)
internal class HeartbeatHandler(
    flow: Flow<Event>,
    private val send: suspend (Command) -> Unit,
    private val restart: suspend () -> Unit,
    private val ping: (Duration) -> Unit,
    private val sequence: Sequence,
    private val ticker: Ticker = Ticker(),
    private val timeSource: TimeSource = TimeSource.Monotonic
) : Handler(flow, "HeartbeatHandler") {

    private val possibleZombie = atomic(false)
    private var timestamp: TimeMark = timeSource.markNow()

    override fun start() {
        on<Event> {
            setNoZombie()
        }

        on<Hello> { hello ->
            ticker.tickAt(hello.heartbeatInterval.toLong()) {
                if (possibleZombie.value) {
                    restart()
                } else {
                    setPossibleZombie()
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


    // https://github.com/Kotlin/kotlinx.atomicfu/issues/180
    private fun setNoZombie() {
        possibleZombie.update { false }
    }

    // https://github.com/Kotlin/kotlinx.atomicfu/issues/180
    private fun setPossibleZombie() {
        possibleZombie.update { true }
    }
}