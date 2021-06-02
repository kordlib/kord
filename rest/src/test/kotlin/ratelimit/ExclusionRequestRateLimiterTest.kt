package dev.kord.rest.ratelimit

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.Clock
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class ExclusionRequestRateLimiterTest : AbstractRequestRateLimiterTest() {

    override fun newRequestRateLimiter(clock: Clock): RequestRateLimiter {
        return ExclusionRequestRateLimiter(clock)
    }

}