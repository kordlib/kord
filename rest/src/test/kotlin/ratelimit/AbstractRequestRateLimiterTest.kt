package dev.kord.rest.ratelimit

import dev.kord.common.entity.DiscordGuild
import dev.kord.rest.request.JsonRequest
import dev.kord.rest.route.Route
import io.ktor.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.seconds
import kotlin.time.toJavaDuration

@ExperimentalTime
@ExperimentalCoroutinesApi
abstract class AbstractRequestRateLimiterTest {

    abstract fun newRequestRateLimiter(clock: Clock): RequestRateLimiter

    private val timeout = 1000.seconds
    private val instant = Instant.EPOCH
    private val RateLimit.Companion.exhausted get() = RateLimit(Total(5), Remaining(0))

    private suspend fun RequestRateLimiter.sendRequest(clock: TestClock, guildId: Long, bucketKey: Long = guildId, rateLimit: RateLimit) {
        val request = JsonRequest<Unit, DiscordGuild>(Route.GuildGet, mapOf(Route.GuildId to guildId.toString()), StringValues.Empty, StringValues.Empty, null)
        val token = await(request)
        when (rateLimit.isExhausted) {
            true -> token.complete(RequestResponse.BucketRateLimit(BucketKey(bucketKey.toString()), rateLimit, Reset(clock.instant().plus(timeout.toJavaDuration()))))
            else -> token.complete(RequestResponse.Accepted(BucketKey(bucketKey.toString()), rateLimit, Reset(clock.instant().plus(timeout.toJavaDuration()))))
        }
    }

    private suspend fun RequestRateLimiter.sendRequest(guildId: Long): RequestToken {
        val request = JsonRequest<Unit, DiscordGuild>(Route.GuildGet, mapOf(Route.GuildId to guildId.toString()), StringValues.Empty, StringValues.Empty, null)
        return await(request)
    }

    private suspend fun RequestToken.complete(clock: TestClock, bucketKey: Long, rateLimit: RateLimit) {
        when (rateLimit.isExhausted) {
            true -> complete(RequestResponse.BucketRateLimit(BucketKey(bucketKey.toString()), rateLimit, Reset(clock.instant().plus(timeout.toJavaDuration()))))
            else -> complete(RequestResponse.Accepted(BucketKey(bucketKey.toString()), rateLimit, Reset(clock.instant().plus(timeout.toJavaDuration()))))
        }
    }

    @Test
    fun `concurrent requests on the same route are handled sequentially`() = runBlockingTest {
        val clock = TestClock(instant, this, ZoneOffset.UTC)
        val rateLimiter = newRequestRateLimiter(clock)

        rateLimiter.sendRequest(1).complete(clock, 1, RateLimit.exhausted) //discovery

        @Suppress("UNUSED_VARIABLE")
        val token = rateLimiter.sendRequest(1) //keep the rate limiter busy

        val token2 = withTimeoutOrNull(10_000) {
            rateLimiter.sendRequest(1)
        }

        assertEquals(null, token2)
    }

    @Test
    fun `a RequestRateLimiter will suspend for rate limited requests with the same identifier`() = runBlockingTest {
        val clock = TestClock(instant, this, ZoneOffset.UTC)
        val rateLimiter = newRequestRateLimiter(clock)

        rateLimiter.sendRequest(clock, 1, rateLimit = RateLimit.exhausted)
        rateLimiter.sendRequest(clock, 1, rateLimit = RateLimit(Total(5), Remaining(5)))

        assertEquals(timeout.inMilliseconds.toLong(), currentTime)
    }

    @Test
    fun `a RequestRateLimiter will suspend for rate limited requests with the same bucket`() = runBlockingTest {
        val clock = TestClock(instant, this, ZoneOffset.UTC)
        val rateLimiter = newRequestRateLimiter(clock)

        rateLimiter.sendRequest(clock, 1, 1, rateLimit = RateLimit.exhausted)
        rateLimiter.sendRequest(clock, 2, 1, rateLimit = RateLimit(Total(5), Remaining(5))) //discovery
        rateLimiter.sendRequest(clock, 2, 1, rateLimit = RateLimit(Total(5), Remaining(5)))

        assertEquals(timeout.inMilliseconds.toLong(), currentTime)
    }

    @Test
    fun `a RequestRateLimiter will not suspend for rate limited requests that don't share an identifier`() = runBlockingTest {
        val clock = TestClock(instant, this, ZoneOffset.UTC)
        val rateLimiter = newRequestRateLimiter(clock)

        rateLimiter.sendRequest(clock, 1, rateLimit = RateLimit.exhausted)
        rateLimiter.sendRequest(clock, 2, rateLimit = RateLimit(Total(5), Remaining(5))) //discovery
        rateLimiter.sendRequest(clock, 2, rateLimit = RateLimit.exhausted)

        assertEquals(0, currentTime)
    }

    @Test
    fun `an exception during the handling won't lock the handler`() = runBlockingTest {
        val clock = TestClock(instant, this, ZoneOffset.UTC)
        val rateLimiter = newRequestRateLimiter(clock)

        rateLimiter.sendRequest(clock, 1, rateLimit = RateLimit(Total(5), Remaining(5)))
        val request = JsonRequest<Unit, DiscordGuild>(Route.GuildGet, mapOf(Route.GuildId to "1"), StringValues.Empty, StringValues.Empty, null)

        try {
            rateLimiter.consume(request) {
                throw IllegalStateException("something went wrong")
            }
        } catch (_: IllegalStateException) {
        }

        withTimeout(1_000_000) {
            rateLimiter.sendRequest(clock, 1, rateLimit = RateLimit.exhausted)
        }
    }

    @Test
    fun `REGRESSION a RequestRateLimiter encountering a non 429 error response will not throw`() = runBlockingTest {
        val clock = TestClock(instant, this, ZoneOffset.UTC)
        val rateLimiter = newRequestRateLimiter(clock)

        rateLimiter.sendRequest(clock, 1, rateLimit = RateLimit(Total(5), Remaining(5))) //discovery
        val token = rateLimiter.sendRequest(1)

        token.complete(RequestResponse.Error)
    }
}