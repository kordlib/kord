package com.gitlab.kordlib.rest.ratelimit

import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.Clock
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@Suppress("EXPERIMENTAL_API_USAGE")
class ParallelRequestRateLimiterTest : AbstractRequestRateLimiterTest() {

    override fun newRequestRateLimiter(clock: Clock): RequestRateLimiter {
        return ParallelRequestRateLimiter(clock)
    }

}