package com.gitlab.kordlib.rest.ratelimit

import com.gitlab.kordlib.rest.request.Request
import com.gitlab.kordlib.rest.request.RequestIdentifier
import com.gitlab.kordlib.rest.request.identifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import mu.KLogger
import mu.KotlinLogging
import java.time.Clock
import java.time.Duration
import java.time.Instant

private val requestLogger = KotlinLogging.logger("[R]:[ExclusionRequestRateLimiter]")

/**
 * A [RequestRateLimiter] that handles all [requests][Request] in sequential order,
 * minimizing the possibility of rate limits. Requests are handled in call order
 * and will suspend to adhere to global and bucket rate limits.
 *
 * @param clock a [Clock] used for calculating suspension times, present for testing purposes.
 */
class ExclusionRequestRateLimiter(clock: Clock = Clock.systemUTC()) : AbstractRateLimiter(clock) {

    override val logger: KLogger get() = requestLogger
    private val sequentialLock = Mutex()

    override suspend fun await(request: Request<*, *>): RequestToken {
        sequentialLock.lock()
        return super.await(request)
    }

    override fun newToken(request: Request<*, *>, buckets: List<Bucket>): RequestToken {
        return ExclusionRequestToken(request.identifier, buckets)
    }

    private inner class ExclusionRequestToken(identity: RequestIdentifier, requestBuckets: List<Bucket>) : AbstractRequestToken(identity, requestBuckets) {

        override suspend fun complete(response: RequestResponse) {
            super.complete(response)
            sequentialLock.unlock()
        }

    }

}
