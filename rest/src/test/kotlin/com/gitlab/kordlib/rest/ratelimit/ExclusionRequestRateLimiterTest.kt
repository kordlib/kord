package com.gitlab.kordlib.rest.ratelimit

import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.Clock
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class ExclusionRequestRateLimiterTest : AbstractRequestRateLimiterTest() {

    override fun newRequestRateLimiter(clock: Clock): RequestRateLimiter {
        return ExclusionRequestRateLimiter(clock)
    }

}