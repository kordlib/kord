package com.gitlab.hopebaron.websocket.retry

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.delay
import mu.KotlinLogging

private val linearRetryLogger = KotlinLogging.logger { }

class LinearRetry(
        private val firstBackoffMillis: Long,
        private val maxBackoffMillis: Long,
        private val maxTries: Int
) : Retry {

    init {
        require(firstBackoffMillis > 0) { "backoff needs to be positive but was $firstBackoffMillis" }
        require(maxBackoffMillis > firstBackoffMillis) { "maxBackoff $maxBackoffMillis needs to be bigger than firstBackoff $firstBackoffMillis" }
        require(maxTries > 0) { "maxTries needs to be positive but was $maxTries" }
    }

    private var tries = atomic(0)

    override val hasNext: Boolean
        get() = tries.value < maxTries

    override fun reset() {
        tries.update { 0 }
    }

    override suspend fun retry() {
        if (!hasNext) error("max retries exceeded")

        tries.incrementAndGet()
        var diff = (maxBackoffMillis - firstBackoffMillis) / maxTries
        diff *= tries.value
        linearRetryLogger.trace { "retry attempt ${tries.value}, delaying for $diff ms" }
        delay(diff)
    }

}
