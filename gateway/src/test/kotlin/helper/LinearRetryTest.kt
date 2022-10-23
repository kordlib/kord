package helper

import dev.kord.gateway.retry.LinearRetry
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import kotlin.time.measureTime

class LinearRetryTest {

    @Test
    fun testLinearity() = runTest {
        val linearRetry = LinearRetry(1.seconds, 10.seconds, 10)
        var i = 0
        val elapsed = TimeSource.Monotonic.measureTime {
            while (linearRetry.hasNext) {
                linearRetry.retry()
                i++
            }
        }

        println(elapsed)
        assert(elapsed >= 55.seconds)
        assert(i == 10)
    }
}