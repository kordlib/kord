package gateway

import dev.kord.common.KordConfiguration
import dev.kord.common.entity.DiscordShard
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.gateway.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.times

private class DelayedStartGateway(private val notifyStarted: suspend () -> Unit) : Gateway {
    override val coroutineContext: CoroutineContext get() = EmptyCoroutineContext
    private val _events = MutableSharedFlow<Event>()
    override val events: SharedFlow<Event> get() = _events
    override val ping: StateFlow<Duration?> = MutableStateFlow(null)

    override suspend fun start(configuration: GatewayConfiguration) {
        delay(START_DELAY)
        _events.emit(READY) // we define a shard as started when it received its ready event
        notifyStarted()
    }

    override suspend fun send(command: Command) {}
    override suspend fun stop() {}
    override suspend fun detach() {}

    companion object {
        val START_DELAY = 500.milliseconds

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
                traces = emptyList(),
            ),
            sequence = 1,
        )
    }
}

class LoginRateLimitingTest {

    private fun testLoginRateLimiting(
        numShards: Int,
        shardIds: Iterable<Int>,
        maxConcurrency: Int,
        expectedBuckets: Int,
    ) = runTest {

        @OptIn(ObsoleteCoroutinesApi::class)
        val startNotificationChannel = actor<Int>(capacity = numShards) {
            var currentlyStartingBucket = -1

            for (shardId in channel) {
                val bucket = shardId / maxConcurrency

                // buckets must be started in order
                assertTrue(bucket >= currentlyStartingBucket)

                currentlyStartingBucket = bucket
            }
        }

        val masterGateway = DefaultMasterGateway(
            shardIds.associateWith { shardId ->
                DelayedStartGateway(notifyStarted = { startNotificationChannel.send(shardId) })
            }
        )

        // this is the function we actually test here
        masterGateway.startWithConfig(
            GatewayConfiguration(
                token = "hunter2",
                name = "Kord",
                DiscordShard(0, numShards),
                threshold = 250,
                intents = Intents(),
            ),
            maxConcurrency,
        )

        val timeSpentLoggingIn = expectedBuckets * DelayedStartGateway.START_DELAY
        val timeSpentWaitingBetweenLogins = (expectedBuckets - 1) * 5.seconds

        val expectedTime = timeSpentLoggingIn + timeSpentWaitingBetweenLogins
        assertEquals(expected = expectedTime.inWholeMilliseconds, actual = currentTime)

        startNotificationChannel.close()
    }

    // probably the most common case
    @Test
    fun `single shard`() =
        testLoginRateLimiting(numShards = 1, shardIds = listOf(0), maxConcurrency = 1, expectedBuckets = 1)

    // https://discord.com/developers/docs/topics/gateway#sharding-max-concurrency
    @Test
    fun `example 1`() =
        testLoginRateLimiting(numShards = 16, shardIds = 0..15, maxConcurrency = 16, expectedBuckets = 1)

    // https://discord.com/developers/docs/topics/gateway#sharding-max-concurrency
    @Test
    fun `example 2`() =
        testLoginRateLimiting(numShards = 32, shardIds = 0..31, maxConcurrency = 16, expectedBuckets = 2)

    // https://discord.com/channels/613425648685547541/697489244649816084/1021565107949551676
    @Test
    fun `example 2 but without shards 15-30`() =
        testLoginRateLimiting(numShards = 32, shardIds = (0..14) + 31, maxConcurrency = 16, expectedBuckets = 1)

    // https://discord.com/channels/556525343595298817/1021384687337353216
    @Test
    fun `Schlaubi's case`() =
        testLoginRateLimiting(numShards = 15, shardIds = 0..14, maxConcurrency = 1, expectedBuckets = 15)

    @Test
    fun `randomly distributed shards`() = testLoginRateLimiting(
        numShards = 24,
        shardIds = listOf(0, 4, 5, 10, 23),
        maxConcurrency = 2,
        expectedBuckets = 3, // started concurrently: [0], [4 and 5], [10 and 23]
    )
}
