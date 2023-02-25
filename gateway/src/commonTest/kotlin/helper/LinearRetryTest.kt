package helper

import dev.kord.gateway.retry.LinearRetry
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

        assertEquals(1000.seconds, elapsed)
    }
}
