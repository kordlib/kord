package dev.kord.rest.ratelimit

import dev.kord.common.entity.DiscordGuild
import dev.kord.rest.request.JsonRequest
import dev.kord.rest.route.Route
import io.ktor.util.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

abstract class AbstractRequestRateLimiterTest {

    abstract fun newRequestRateLimiter(clock: Clock): RequestRateLimiter

    private val timeout = 1000.seconds
    private val instant = Instant.fromEpochMilliseconds(0)
    private val RateLimit.Companion.exhausted get() = RateLimit(Total(5), Remaining(0))

    private suspend fun RequestRateLimiter.sendRequest(
        clock: TestClock,
        guildId: Long,
        bucketKey: Long = guildId,
        rateLimit: RateLimit
    ) {
        val request = JsonRequest<Unit, DiscordGuild>(
            Route.GuildGet,
            mapOf(Route.GuildId to guildId.toString()),
            StringValues.Empty,
            StringValues.Empty,
            null
        )
        val token = await(request)
        when (rateLimit.isExhausted) {
            true -> token.complete(
                RequestResponse.BucketRateLimit(
                    BucketKey(bucketKey.toString()),
                    rateLimit,
                    Reset(clock.now().plus(timeout))
                )
            )
            else -> token.complete(
                RequestResponse.Accepted(
                    BucketKey(bucketKey.toString()),
                    rateLimit,
                    Reset(clock.now().plus(timeout))
                )
            )
        }
    }

    private suspend fun RequestRateLimiter.sendRequest(guildId: Long): RequestToken {
        val request = JsonRequest<Unit, DiscordGuild>(
            Route.GuildGet,
            mapOf(Route.GuildId to guildId.toString()),
            StringValues.Empty,
            StringValues.Empty,
            null
        )
        return await(request)
    }

    private suspend fun RequestToken.complete(clock: TestClock, bucketKey: Long, rateLimit: RateLimit) {
        when (rateLimit.isExhausted) {
            true -> complete(
                RequestResponse.BucketRateLimit(
                    BucketKey(bucketKey.toString()),
                    rateLimit,
                    Reset(clock.now().plus(timeout))
                )
            )
            else -> complete(
                RequestResponse.Accepted(
                    BucketKey(bucketKey.toString()),
                    rateLimit,
                    Reset(clock.now().plus(timeout))
                )
            )
        }
    }

    @Test
    fun `concurrent requests on the same route are handled sequentially`() = runTest {
        val clock = TestClock(instant, this)
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
    fun `a RequestRateLimiter will suspend for rate limited requests with the same identifier`() = runTest {
        val clock = TestClock(instant, this)
        val rateLimiter = newRequestRateLimiter(clock)

        rateLimiter.sendRequest(clock, 1, rateLimit = RateLimit.exhausted)
        rateLimiter.sendRequest(clock, 1, rateLimit = RateLimit(Total(5), Remaining(5)))

        assertEquals(timeout.inWholeMilliseconds, currentTime)
    }

    @Test
    fun `a RequestRateLimiter will suspend for rate limited requests with the same bucket`() = runTest {
        val clock = TestClock(instant, this)
        val rateLimiter = newRequestRateLimiter(clock)

        rateLimiter.sendRequest(clock, 1, 1, rateLimit = RateLimit.exhausted)
        rateLimiter.sendRequest(clock, 1, 1, rateLimit = RateLimit(Total(5), Remaining(5))) //discovery
        rateLimiter.sendRequest(clock, 1, 1, rateLimit = RateLimit(Total(5), Remaining(5)))

        assertEquals(timeout.inWholeMilliseconds, currentTime)
    }

    @Test
    fun `a RequestRateLimiter will not suspend for rate limited requests that don't share an identifier`() = runTest {
        val clock = TestClock(instant, this)
        val rateLimiter = newRequestRateLimiter(clock)

        rateLimiter.sendRequest(clock, 1, rateLimit = RateLimit.exhausted)
        rateLimiter.sendRequest(clock, 2, rateLimit = RateLimit(Total(5), Remaining(5))) //discovery
        rateLimiter.sendRequest(clock, 2, rateLimit = RateLimit.exhausted)

        assertEquals(0, currentTime)
    }

    @Test
    fun `an exception during the handling won't lock the handler`() = runTest {
        val clock = TestClock(instant, this)
        val rateLimiter = newRequestRateLimiter(clock)

        rateLimiter.sendRequest(clock, 1, rateLimit = RateLimit(Total(5), Remaining(5)))
        val request = JsonRequest<Unit, DiscordGuild>(
            Route.GuildGet,
            mapOf(Route.GuildId to "1"),
            StringValues.Empty,
            StringValues.Empty,
            null
        )

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
    fun `REGRESSION a RequestRateLimiter encountering a non 429 error response will not throw`() = runTest {
        val clock = TestClock(instant, this)
        val rateLimiter = newRequestRateLimiter(clock)

        rateLimiter.sendRequest(clock, 1, rateLimit = RateLimit(Total(5), Remaining(5))) //discovery
        val token = rateLimiter.sendRequest(1)

        token.complete(RequestResponse.Error)
    }
}
