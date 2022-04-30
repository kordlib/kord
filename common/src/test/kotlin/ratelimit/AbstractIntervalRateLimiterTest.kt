package ratelimit

import dev.kord.common.ratelimit.IntervalRateLimiter
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.INFINITE
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds

abstract class AbstractIntervalRateLimiterTest {

    private val limit = 120
    private val interval = 60.seconds

    abstract fun newRateLimiter(limit: Int, interval: Duration): IntervalRateLimiter

    private lateinit var rateLimiter: IntervalRateLimiter

    @BeforeTest
    fun setup() {
        rateLimiter = newRateLimiter(limit, interval)
    }

    @Test
    fun `an interval rate limiter does not accept illegal arguments`() {

        fun assertIAE(limit: Int, interval: Duration) {
            assertFailsWith<IllegalArgumentException> { newRateLimiter(limit, interval) }
        }

        assertIAE(limit = 0, interval) // 0 limit
        assertIAE(limit = -864, interval) // negative limit
        assertIAE(limit, interval = ZERO) // 0 interval
        assertIAE(limit, interval = -(673.seconds)) // negative interval
        assertIAE(limit, interval = INFINITE) // infinite interval
    }

    @Test
    fun `an interval rate limiter does not ratelimit when under limit`() = runTest {
        repeat(limit) { rateLimiter.consume() }

        assertEquals(expected = 0L, actual = currentTime, "expected timeout of 0 ms but was $currentTime ms")
    }

    @Test
    fun `an interval rate limiter does ratelimit when over limit`() = runTest {
        repeat(limit + 1) { rateLimiter.consume() }

        assertEquals(
            expected = interval.inWholeMilliseconds,
            actual = currentTime,
            "expected timeout of ${interval.inWholeMilliseconds} ms but was $currentTime ms",
        )

        repeat(limit) { rateLimiter.consume() }

        assertEquals(
            expected = (interval * 2).inWholeMilliseconds,
            actual = currentTime,
            "expected timeout of ${(interval * 2).inWholeMilliseconds} ms but was $currentTime ms"
        )
    }
}
