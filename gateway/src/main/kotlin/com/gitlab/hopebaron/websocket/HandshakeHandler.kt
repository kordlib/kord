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
        flow:  Flow<Event>,
        send: suspend (Command) -> Unit,
        configuration: GatewayConfiguration
) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    private var session: AtomicRef<String?> = atomic(null)
    private var sequence: AtomicRef<Int?> = atomic(null)

    init {
        launch {
            flow.filterIsInstance<DispatchEvent>().collect { event ->
                sequence.update { event.sequence ?: sequence.value }
            }
        }

        launch {
            flow.filterIsInstance<Ready>().collect {
                event -> session.update { event.data.sessionId }
            }
        }

        launch {
            flow.filterIsInstance<Hello>().collect {
                if (sequence.value == null) {
                    val identify = Identify(
                            configuration.token,
                            IdentifyProperties(os, configuration.name, configuration.name),
                            false,
                            shard = configuration.shard,
                            presence = configuration.presence
                    )
                    send(identify)
                } else send(Resume(configuration.token, session.value!!, sequence.value))
            }
        }

        launch {
            flow.filterIsInstance<Close>().collect {
                session.update { null }
                sequence.update { null }
            }
        }

        launch {
            flow.filterIsInstance<CloseForReconnect>().collect {
                this@HandshakeHandler.cancel()
            }
        }
    }

}
