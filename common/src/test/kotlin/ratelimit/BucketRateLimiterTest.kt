package ratelimit

import com.gitlab.hopebaron.common.ratelimit.BucketRateLimiter
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.Duration
import kotlin.system.measureTimeMillis

class BucketRateLimiterTest : Spek({

    describe("a bucket rate limiter") {
        val interval = Duration.ofMillis(250)
        val rateLimiter by memoized { BucketRateLimiter(100, interval) }

        it("does not rateLimit when under capacity") {
            val time = measureTimeMillis {
                repeat(99) {
                    runBlocking {
                        rateLimiter.consume()
                    }
                }
            }

            val duration = Duration.ofMillis(time)
            assert(duration < interval) { "${duration.toMillis()} was bigger than interval ${duration.toMillis()}" }
        }

        it("does rateLimit when over capacity") {
            val time = measureTimeMillis {
                repeat(101) {
                    runBlocking {
                        rateLimiter.consume()
                    }
                }
            }

            val duration = Duration.ofMillis(time)
            assert(duration > interval) { "${duration.toMillis()} was smaller than interval ${duration.toMillis()}" }
        }

    }

})