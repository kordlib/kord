package com.gitlab.kordlib.gateway.handler

import com.gitlab.kordlib.gateway.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

internal class HandshakeHandler(
        flow: Flow<Event>,
        private val send: suspend (Command) -> Unit,
        private val sequence: Sequence
) : Handler(flow) {

    lateinit var configuration: GatewayConfiguration

    private val session: AtomicRef<String?> = atomic(null)

    private val identify
        get() = configuration.identify

    private val resume
        get() = Resume(configuration.token, session.value!!, sequence.value)

    private val sessionStart get() = session.value == null

    override fun start() {
        on<Ready> { event ->
            session.update { event.data.sessionId }
        }

        on<Hello> {
            if (sessionStart) send(identify)
            else send(resume)
        }

        on<SessionClose> {
            session.update { null }
        }
    }
}
