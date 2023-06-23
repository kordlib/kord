package dev.kord.rest.ratelimit

import kotlinx.datetime.Clock

class ExclusionRequestRateLimiterTest : AbstractRequestRateLimiterTest() {

    override fun newRequestRateLimiter(clock: Clock): RequestRateLimiter {
        return ExclusionRequestRateLimiter(clock)
    }

}
