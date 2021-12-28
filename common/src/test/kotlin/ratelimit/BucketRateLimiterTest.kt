package ratelimit

import dev.kord.common.ratelimit.BucketRateLimiter
import fixed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.asserter
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class BucketRateLimiterTest {

    val interval = 1_000_000.milliseconds
    val instant = Clock.System.now()
    val clock = Clock.fixed(instant)
    lateinit var rateLimiter: BucketRateLimiter

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
    fun `a bucket rate limiter does ratelimit when over capacity`(): Unit = runBlockingTest {
        rateLimiter.consume()
        rateLimiter.consume()

        asserter.assertTrue(
            "expected timeout of ${interval.inWholeMilliseconds} ms but was $currentTime ms",
            interval.inWholeMilliseconds == currentTime
        )
    }

}
