package dev.kord.rest.ratelimit

import dev.kord.rest.request.Request
import dev.kord.rest.request.RequestIdentifier
import dev.kord.rest.request.identifier
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.Clock
import mu.KLogger
import mu.KotlinLogging

private val requestLogger = KotlinLogging.logger {}

/**
 * A [RequestRateLimiter] that handles all [requests][Request] in sequential order,
 * minimizing the possibility of rate limits. Requests are handled in call order
 * and will suspend to adhere to global and bucket rate limits.
 *
 * @param clock a [Clock] used for calculating suspension times, present for testing purposes.
 */
public class ExclusionRequestRateLimiter(clock: Clock = Clock.System) : AbstractRateLimiter(clock) {

    override val logger: KLogger get() = requestLogger
    private val sequentialLock = Mutex()

    override suspend fun await(request: Request<*, *>): RequestToken {
        sequentialLock.lock()
        return super.await(request)
    }

    override fun newToken(request: Request<*, *>, buckets: List<Bucket>): RequestToken {
        return ExclusionRequestToken(this, request.identifier, buckets)
    }

    private inner class ExclusionRequestToken(
        rateLimiter: ExclusionRequestRateLimiter,
        identity: RequestIdentifier,
        requestBuckets: List<Bucket>
    ) :
        AbstractRequestToken(rateLimiter, identity, requestBuckets) {

        override suspend fun complete(response: RequestResponse) {
            super.complete(response)
            sequentialLock.unlock()
        }

    }

}
