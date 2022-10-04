package ratelimit

import dev.kord.common.KordConfiguration
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Close.DiscordClose
import dev.kord.gateway.GatewayCloseCode.Unknown
import dev.kord.gateway.Ready
import dev.kord.gateway.ReadyData
import dev.kord.gateway.ratelimit.IdentifyRateLimiter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows
import kotlin.coroutines.ContinuationInterceptor
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.times

private val START_DELAY = 500.milliseconds

private val READY = Ready(
    data = ReadyData(
        version = KordConfiguration.GATEWAY_VERSION,
        user = DiscordUser(
            id = Snowflake(42),
            username = "username",
            discriminator = "1337",
            avatar = null,
        ),
        privateChannels = emptyList(),
        guilds = emptyList(),
        sessionId = "deadbeef",
        resumeGatewayUrl = "wss://us-east1-b.gateway.discord.gg",
        traces = emptyList(),
    ),
    sequence = 1,
)

private val CLOSE = DiscordClose(closeCode = Unknown, recoverable = true)

class IdentifyRateLimiterTest {

    @Test
    fun `IdentifyRateLimiter throws IAEs`() = runBlocking<Unit> {
        assertThrows<IllegalArgumentException> { IdentifyRateLimiter(maxConcurrency = 0) }
        assertThrows<IllegalArgumentException> { IdentifyRateLimiter(maxConcurrency = -1) }
        assertThrows<IllegalArgumentException> {
            val rateLimiter = IdentifyRateLimiter(maxConcurrency = 1)
            rateLimiter.consume(-1, emptyFlow())
        }
    }

    private fun expectedTime(buckets: Int): Long {
        require(buckets >= 1)

        val timeSpentLoggingIn = buckets * START_DELAY
        val timeSpentWaitingBetweenLogins = (buckets - 1) * 5.seconds
        return (timeSpentLoggingIn + timeSpentWaitingBetweenLogins).inWholeMilliseconds
    }

    private fun testRateLimiter(
        shardIds: Iterable<Int>,
        maxConcurrency: Int,
        expectedBuckets: Int,
    ) = runTest {
        val rateLimiter = IdentifyRateLimiter(
            maxConcurrency,
            dispatcher = coroutineContext[ContinuationInterceptor] as CoroutineDispatcher,
        )
        assertEquals(expected = maxConcurrency, actual = rateLimiter.maxConcurrency)

        val shardCount = shardIds.count()

        class Start(val time: Long, val shardId: Int)

        val startChannel = Channel<Start>(capacity = shardCount)

        for (shardId in shardIds) {
            val startPermission = CompletableDeferred<Unit>()

            val mockEvents = flow {
                startPermission.await()
                delay(START_DELAY)
                startChannel.send(Start(currentTime, shardId))
                val close = Random.nextDouble() > 0.8
                emit(if (close) CLOSE else READY)
            }.shareIn(this, started = Eagerly) // hot flow like Gateway.events

            // here we test the IdentifyRateLimiter
            launch {
                rateLimiter.consume(shardId, mockEvents)
                startPermission.complete(Unit)
            }
        }

        // wait for all starts
        val starts = startChannel.consumeAsFlow().take(shardCount).toList()

        // test that the correct amount of total time has elapsed
        assertEquals(expectedTime(expectedBuckets), actual = currentTime)

        class Bucket(val startTime: Long, val shardIds: List<Int>)

        val buckets = starts
            .groupBy(Start::time, Start::shardId)
            .map { (time, shardIds) -> Bucket(time, shardIds) }
            .sortedBy { it.startTime }

        buckets.forEachIndexed { currentBucketIndex, currentBucket ->
            val currentBucketCount = currentBucketIndex + 1

            // test time for each bucket
            assertEquals(expectedTime(currentBucketCount), actual = currentBucket.startTime)

            // buckets must not be bigger than maxConcurrency
            assertTrue(currentBucket.shardIds.size <= maxConcurrency)

            // buckets must not have duplicate `rate_limit_key`s
            val rateLimitKeys = currentBucket.shardIds.map { it % maxConcurrency }
            assertEquals(expected = rateLimitKeys.distinct(), actual = rateLimitKeys)

            // all later buckets must have higher shardIds
            assertTrue(
                buckets.drop(currentBucketCount).all { laterBucket ->
                    laterBucket.shardIds.all { laterShardId ->
                        currentBucket.shardIds.all { it < laterShardId }
                    }
                }
            )
        }
    }


    // probably the most common case
    @Test
    fun `single shard`() = testRateLimiter(shardIds = listOf(0), maxConcurrency = 1, expectedBuckets = 1)

    // https://discord.com/developers/docs/topics/gateway#sharding-max-concurrency
    @Test
    fun `example 1`() = testRateLimiter(shardIds = 0..15, maxConcurrency = 16, expectedBuckets = 1)

    // https://discord.com/developers/docs/topics/gateway#sharding-max-concurrency
    @Test
    fun `example 2`() = testRateLimiter(shardIds = 0..31, maxConcurrency = 16, expectedBuckets = 2)

    // https://discord.com/channels/613425648685547541/697489244649816084/1021565107949551676
    @Test
    fun `example 2 but without shards 15-30`() =
        testRateLimiter(shardIds = (0..14) + 31, maxConcurrency = 16, expectedBuckets = 1)

    // https://discord.com/channels/556525343595298817/1021384687337353216
    @Test
    fun `Schlaubi's case`() = testRateLimiter(shardIds = 0..14, maxConcurrency = 1, expectedBuckets = 15)

    @Test
    fun `randomly distributed shards`() = testRateLimiter(
        shardIds = listOf(0, 4, 5, 10, 23),
        maxConcurrency = 2,
        expectedBuckets = 3, // started concurrently: [0], [4 and 5], [10 and 23]
    )
}
