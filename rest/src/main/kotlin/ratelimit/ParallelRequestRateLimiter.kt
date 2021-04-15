package dev.kord.rest.ratelimit

import dev.kord.common.annotation.KordUnsafe
import dev.kord.rest.request.Request
import dev.kord.rest.request.RequestIdentifier
import dev.kord.rest.request.identifier
import mu.KLogger
import mu.KotlinLogging
import java.time.Clock

private val parallelLogger = KotlinLogging.logger {}

/**
 * A [RequestRateLimiter] that tries to handle [requests][Request] in a parallel order.
 * Requests are run sequentially per bucket,
 * allowing requests that do not share a common rate limit to run uninterrupted.
 * Requests that share a common bucket are handled in call order
 * and will suspend to adhere to global and bucket rate limits.
 *
 * Using this [RequestRateLimiter] increases the chance of a exceeding the global rate limit, which in exceedingly
 * unlikely cases might result in your bot's account getting temporarily banned.
 * As such, the [ExclusionRequestRateLimiter] is generally preferred.
 *
 * @param clock a [Clock] used for calculating suspension times, present for testing purposes.
 */
@KordUnsafe
class ParallelRequestRateLimiter(clock: Clock = Clock.systemUTC()) : AbstractRateLimiter(clock) {

    override val logger: KLogger
        get() = parallelLogger

    override fun newToken(request: Request<*, *>, buckets: List<Bucket>): RequestToken =
        ParallelRequestToken(request.identifier, buckets)

    private inner class ParallelRequestToken(identity: RequestIdentifier, requestBuckets: List<Bucket>) :
        AbstractRequestToken(identity, requestBuckets)

}