package dev.kord.rest.ratelimit

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.Clock
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@Suppress("EXPERIMENTAL_API_USAGE")
class ParallelRequestRateLimiterTest : AbstractRequestRateLimiterTest() {

    override fun newRequestRateLimiter(clock: Clock): RequestRateLimiter {
        return ParallelRequestRateLimiter(clock)
    }

}