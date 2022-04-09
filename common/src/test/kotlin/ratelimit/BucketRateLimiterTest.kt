@file:Suppress("DEPRECATION")

package ratelimit

import dev.kord.common.ratelimit.BucketRateLimiter
import fixed
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.asserter
import kotlin.time.Duration.Companion.milliseconds

class BucketRateLimiterTest {

    private val interval = 1_000_000.milliseconds
    private val instant = Clock.System.now()
    private val clock = Clock.fixed(instant)
    private lateinit var rateLimiter: BucketRateLimiter

    @BeforeTest
    fun setup() {
        rateLimiter = BucketRateLimiter(1, interval, clock)
    }

    @Test
    fun `a bucket rate limiter does not ratelimit when under capacity`(): Unit = runTest {
        rateLimiter.consume()

        asserter.assertTrue("expected timeout of 0 ms but was $currentTime ms", 0L == currentTime)
    }

    @Test
    fun `a bucket rate limiter does ratelimit when over capacity`(): Unit = runTest {
        rateLimiter.consume()
        rateLimiter.consume()

        asserter.assertTrue(
            "expected timeout of ${interval.inWholeMilliseconds} ms but was $currentTime ms",
            interval.inWholeMilliseconds == currentTime
        )
    }

}
