package dev.kord.voice.gateway.handler

import dev.kord.voice.gateway.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
) : Handler(flow, "HeartbeatHandler") {
    private var timestamp: TimeMark = timeSource.markNow()

    override fun start() {
        on<Hello> {
            ticker.tickAt(it.heartbeatInterval.toLong()) {
                timestamp = timeSource.markNow()
                send(Heartbeat(timestamp.elapsedNow().inWholeMilliseconds))
            }
        }

        on<HeartbeatAck> {
            ping(timestamp.elapsedNow())
        }
    }
}