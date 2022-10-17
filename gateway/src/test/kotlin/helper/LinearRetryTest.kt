package helper

import dev.kord.gateway.retry.LinearRetry
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class LinearRetryTest {

    @Test
    fun testLinearity() {
        val linearRetry = LinearRetry(1.milliseconds, 10.milliseconds, 10)
        val startTime = System.currentTimeMillis()
        var i = 0
        runBlocking {
            while (linearRetry.hasNext) {
                linearRetry.retry()
                i++
            }
        }
        assert(System.currentTimeMillis() > (startTime + 55))
        assert(i == 10)
    }
}