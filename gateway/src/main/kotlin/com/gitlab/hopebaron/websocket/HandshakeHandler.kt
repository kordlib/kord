package com.gitlab.hopebaron.websocket

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.coroutines.CoroutineContext

@FlowPreview
class HandshakeHandler(
        flow: Flow<Event>,
        send: suspend (Command) -> Unit,
        private val configuration: GatewayConfiguration
) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    private var session: AtomicRef<String?> = atomic(null)
    private var sequence: AtomicRef<Int?> = atomic(null)

    private val identify
        get() = Identify(
                configuration.token,
                IdentifyProperties(os, configuration.name, configuration.name),
                false,
                shard = configuration.shard,
                presence = configuration.presence
        )

    private val resume
        get() = Resume(configuration.token, session.value!!, sequence.value)

    private val sessionStart get() = sequence.value == null

    init {
        flow.on<DispatchEvent> { event ->
            sequence.update { event.sequence ?: sequence.value }
        }

        flow.on<Ready> { event ->
            session.update { event.data.sessionId }
        }

        flow.on<Hello> {
            if (sessionStart) send(identify)
            else send(resume)
        }

        flow.on<Close> {
            this@HandshakeHandler.cancel()
        }

        flow.on<CloseForReconnect> {
            this@HandshakeHandler.cancel()
        }
    }

    private inline fun <reified T> Flow<Event>.on(crossinline block: suspend (T) -> Unit) {
        launch {
            filterIsInstance<T>().collect { block(it) }
        }
    }

}
