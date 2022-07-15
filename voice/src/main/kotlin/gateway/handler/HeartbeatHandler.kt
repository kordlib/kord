package dev.kord.voice.gateway.handler

import dev.kord.voice.gateway.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@OptIn(ObsoleteCoroutinesApi::class)
internal class HeartbeatHandler(
    flow: Flow<VoiceEvent>,
    private val send: suspend (Command) -> Unit,
    private val ping: (Duration) -> Unit,
    private val ticker: Ticker = Ticker(),
    private val timeSource: TimeSource = TimeSource.Monotonic
) : GatewayEventHandler(flow, "HeartbeatHandler") {
    private var timestamp: TimeMark = timeSource.markNow()
    private var interval by atomic(0L)

    override suspend fun start() = coroutineScope {
        on<Hello> {
            interval = it.heartbeatInterval.toLong()
        }

        on<Ready> {
            launch {
                ticker.tickAt(interval) {
                    send(Heartbeat(timestamp.elapsedNow().inWholeMilliseconds))
                    timestamp = timeSource.markNow()
                }
            }
        }

        on<HeartbeatAck> {
            ping(timestamp.elapsedNow())
        }

        on<Close> {
            ticker.stop()
        }
    }
}
