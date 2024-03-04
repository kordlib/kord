package dev.kord.gateway.retry

import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class LinearRetryTest {

    @Test
    fun testLinearity() = runTest {
        val linearRetry = LinearRetry(1.seconds, 10.seconds, 10)
        var i = 0
        val start = currentTime

        while (linearRetry.hasNext) {
            linearRetry.retry()
            i++
        }

        val end = currentTime
        val elapsed = (end - start).milliseconds

        assertEquals(55.seconds, elapsed)
        assertEquals(10, i)
    }

    @Test
    fun testExtreme() = runTest {
        val linearRetry = LinearRetry(1.seconds, 60.seconds, Int.MAX_VALUE)
        var i = 0
        val start = currentTime

        while (linearRetry.hasNext && i < 1000) {
            linearRetry.retry()
            i++
        }

        val end = currentTime
        val elapsed = (end - start).milliseconds

        // first retry is exactly 1s, retries 2 to 1000 are between 1.000000027s and 1.000027447s,
        // kotlinx.coroutines.delay (used by LinearRetry) rounds nanoseconds up to full milliseconds since 1.8.0:
        // https://github.com/Kotlin/kotlinx.coroutines/pull/3921
        // -> 1s + 999 * 1.001s = 1000.999s
        assertEquals(1000.seconds + 999.milliseconds, elapsed)
    }
}
