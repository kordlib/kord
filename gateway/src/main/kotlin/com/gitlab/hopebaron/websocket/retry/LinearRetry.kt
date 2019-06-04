package com.gitlab.hopebaron.websocket.retry

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.*

class LinearRetry(
        private val firstBackoffMillis: Long,
        private val maxBackoffMillis: Long,
        private val maxTries: Int
) : Retry {

    private var tries = atomic(0)

    override val hasNext: Boolean
        get() = tries.value < maxTries

    override fun reset() {
        tries.update { 0 }
    }

    override suspend fun retry() {
        var diff = (firstBackoffMillis - maxBackoffMillis) / maxTries
        diff *= tries.value
        delay(diff)
        GlobalScope.launch { }

    }

}
