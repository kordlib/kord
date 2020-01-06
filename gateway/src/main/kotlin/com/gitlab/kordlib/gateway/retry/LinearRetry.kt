package com.gitlab.kordlib.gateway.retry

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration
import kotlin.time.milliseconds

private val linearRetryLogger = KotlinLogging.logger { }

/**
 * A Retry that linearly increases the delay time between a given minimum and maximum over a given amount of tries.
 *
 * @param firstBackoffMillis the initial delay for a [retry] invocation.
 * @param maxBackoffMillis the maximum delay for a [retry] invocation.
 * @param maxTries the maximum amount of consecutive retries before [hasNext] returns false.
 */
class LinearRetry constructor(
        private val firstBackoff: Duration,
        private val maxBackoff: Duration,
        private val maxTries: Int
) : Retry {

    constructor(firstBackoffMillis: Long, maxBackoffMillis: Long, maxTries: Int) :
            this(firstBackoffMillis.milliseconds, maxBackoffMillis.milliseconds, maxTries)

    init {
        require(firstBackoff.isPositive()) { "firstBackoff needs to be positive but was ${firstBackoff.toLongMilliseconds()} ms" }
        require(maxBackoff.isPositive()) { "maxBackoff needs to be positive but was ${maxBackoff.toLongMilliseconds()} ms" }
        require(maxBackoff.minus(firstBackoff).isPositive()) { "maxBackoff ${maxBackoff.toLongMilliseconds()} ms needs to be bigger than firstBackoff ${firstBackoff.toLongMilliseconds()} ms" }
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
        var diff = (maxBackoff - firstBackoff).toLongMilliseconds() / maxTries
        diff *= tries.value
        linearRetryLogger.trace { "retry attempt ${tries.value}, delaying for $diff ms" }
        delay(diff)
    }

}
