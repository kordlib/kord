package ratelimit

import dev.kord.common.ratelimit.BucketRateLimiter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.asserter
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
@ExperimentalCoroutinesApi
class BucketRateLimiterTest {

    val interval = 1_000_000.milliseconds
    val instant = Instant.now()
    val clock = Clock.fixed(instant, ZoneOffset.UTC)
    lateinit var rateLimiter: BucketRateLimiter

    @BeforeTest
    fun setup() {
        rateLimiter = BucketRateLimiter(1, interval, clock)
    }

    @Test
    fun `a bucket rate limiter does not ratelimit when under capacity`(): Unit = runBlockingTest {
        rateLimiter.consume()

        asserter.assertTrue("expected timeout of 0 ms but was $currentTime ms", 0L == currentTime)
    }

    @Test
    fun `a bucket rate limiter does ratelimit when over capacity`(): Unit = runBlockingTest {
        rateLimiter.consume()
        rateLimiter.consume()

        asserter.assertTrue("expected timeout of ${interval.inMilliseconds.toLong()} ms but was $currentTime ms", interval.toLongMilliseconds() == currentTime)
    }

}
