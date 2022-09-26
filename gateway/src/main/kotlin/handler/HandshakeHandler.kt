package dev.kord.gateway.handler

import dev.kord.gateway.*
import dev.kord.gateway.retry.Retry
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.flow.Flow

internal class HandshakeHandler(
    flow: Flow<Event>,
    private val send: suspend (Command) -> Unit,
    private val sequence: Sequence,
    private val reconnectRetry: Retry
) : Handler(flow, "HandshakeHandler") {

    lateinit var configuration: GatewayConfiguration

    private val session: AtomicRef<String?> = atomic(null)

    private val identify
        get() = configuration.identify

    private val resume
        get() = Resume(configuration.token, session.value!!, sequence.value ?: 0)

    val needsIdentify get() = session.value == null

    override fun start() {
        on<Ready> { event ->
            session.update { event.data.sessionId }
        }

        on<Hello> {
            reconnectRetry.reset() //connected and read without problems, resetting retry counter
            send(if (needsIdentify) identify else resume)
        }

        on<Close.SessionReset> {
            session.update { null }
        }
    }
}
