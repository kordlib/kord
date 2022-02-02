package dev.kord.rest.ratelimit

import dev.kord.common.annotation.KordUnsafe
import kotlinx.datetime.Clock

class ParallelRequestRateLimiterTest : AbstractRequestRateLimiterTest() {

    @OptIn(KordUnsafe::class)
    override fun newRequestRateLimiter(clock: Clock): RequestRateLimiter {
        return ParallelRequestRateLimiter(clock)
    }

}
