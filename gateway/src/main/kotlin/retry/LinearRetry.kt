package dev.kord.gateway.retry

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration

private val linearRetryLogger = KotlinLogging.logger { }

/**
 * A Retry that linearly increases the delay time between a given minimum and maximum over a given amount of tries.
 *
 * @param firstBackoff the initial delay for a [retry] invocation.
 * @param maxBackoff the maximum delay for a [retry] invocation.
 * @param maxTries the maximum amount of consecutive retries before [hasNext] returns false.
 */
public class LinearRetry(
    private val firstBackoff: Duration,
    private val maxBackoff: Duration,
    private val maxTries: Int
) : Retry {

    init {
        require(firstBackoff.isPositive()) { "firstBackoff needs to be positive but was ${firstBackoff.inWholeMilliseconds} ms" }
        require(maxBackoff.isPositive()) { "maxBackoff needs to be positive but was ${maxBackoff.inWholeMilliseconds} ms" }
        require(
            maxBackoff.minus(firstBackoff).isPositive()
        ) { "maxBackoff ${maxBackoff.inWholeMilliseconds} ms needs to be bigger than firstBackoff ${firstBackoff.inWholeMilliseconds} ms" }
        require(maxTries > 0) { "maxTries needs to be positive but was $maxTries" }
    }

    private val tries = atomic(0)

    override val hasNext: Boolean
        get() = tries.value < maxTries

    override fun reset() {
        tries.update { 0 }
    }

    override suspend fun retry() {
        if (!hasNext) error("max retries exceeded")

        tries.incrementAndGet()
        var diff = (maxBackoff - firstBackoff).inWholeMilliseconds / maxTries
        diff *= tries.value
        linearRetryLogger.trace { "retry attempt ${tries.value}, delaying for $diff ms" }
        delay(diff)
    }

}
