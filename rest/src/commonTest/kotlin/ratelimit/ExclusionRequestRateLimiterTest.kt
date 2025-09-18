package dev.kord.rest.ratelimit

import kotlin.time.Clock

class ExclusionRequestRateLimiterTest : AbstractRequestRateLimiterTest() {

    override fun newRequestRateLimiter(clock: Clock): RequestRateLimiter {
        return ExclusionRequestRateLimiter(clock)
    }

}
