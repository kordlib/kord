package com.gitlab.hopebaron.websocket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HandshakeHandler(
        private val flow: Flow<Event>,
        private val send: suspend (Command) -> Unit
) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    var session: String? = null
    var sequence: Int? = null

    private val jobs = mutableListOf<Job>()

    fun start(configuration: GatewayConfiguration) {
        jobs += launch {
            flow.filterIsInstance<Ready>().collect { ready ->
                session = ready.data.sessionId
            }
        }

        jobs += launch {
            flow.filterIsInstance<Hello>().collect {
                if (session == null) {
                    val identify = Identify(
                            configuration.token,
                            IdentifyProperties(os, configuration.name, configuration.name),
                            false,
                            shard = configuration.shard,
                            presence = configuration.presence
                    )
                    send(identify)
                } else {
                    send(Resume(configuration.token, session!!, sequence))
                }

            }
        }
    }

    fun stop() {
        jobs.forEach { it.cancel() }
    }
}
