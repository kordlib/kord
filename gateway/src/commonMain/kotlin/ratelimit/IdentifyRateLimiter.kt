package dev.kord.gateway.ratelimit

import dev.kord.gateway.*
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

/**
 * A rate limiter that follows [Discord's rate limits](https://discord.com/developers/docs/topics/gateway#rate-limiting)
 * for [Gateway]s when [identifying][Identify].
 *
 * The [additional requirements](https://discord.com/developers/docs/topics/gateway#sharding-max-concurrency) for
 * multiple shards are also taken into account.
 */
public interface IdentifyRateLimiter {

    /** The number of [Identify] requests allowed per 5 seconds. */
    public val maxConcurrency: Int

    /**
     * Suspends until the [Gateway] with the given [shardId] is allowed to [Identify].
     *
     * Identifying is considered complete once [Ready], [InvalidSession] or [Close] is observed in [events].
     *
     * @throws IllegalArgumentException if [shardId] is negative.
     */
    public suspend fun consume(shardId: Int, events: SharedFlow<Event>)
}


/**
 * Creates a new [IdentifyRateLimiter] with the given [maxConcurrency].
 *
 * The [CoroutineDispatcher] used for launching coroutines inside this [IdentifyRateLimiter] can be configured with
 * [dispatcher]. [Dispatchers.Default] is used by default.
 *
 * @throws IllegalArgumentException if [maxConcurrency] is not positive.
 */
public fun IdentifyRateLimiter(
    maxConcurrency: Int,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): IdentifyRateLimiter {
    require(maxConcurrency > 0) { "maxConcurrency must be positive but was $maxConcurrency" }
    return IdentifyRateLimiterImpl(maxConcurrency, dispatcher)
}

private val logger = KotlinLogging.logger { }

private class IdentifyRateLimiterImpl(
    override val maxConcurrency: Int,
    private val dispatcher: CoroutineDispatcher,
) : IdentifyRateLimiter {

    // These mutexes are fair (according to the documentation of Mutex and its factory function), we rely on this to
    // guarantee a fair rate limiter behavior.
    // rateLimitKey is always in the range 0..<maxConcurrency, so size has to be maxConcurrency (one per rateLimitKey)
    private val mutexesByRateLimitKey = Array(size = maxConcurrency) { Mutex() }

    // scope.cancel() is never called, but that's ok: all coroutines that are launched in this scope complete after
    // waiting for the previous one trying to lock the same mutex and a fixed delay of at most DELAY_AFTER_IDENTIFY +
    // IDENTIFY_TIMEOUT -> no resource cleanup needed
    // SupervisorJob is used to ensure an unexpected failure in one coroutine doesn't leave the rate limiter unusable
    private val scope = CoroutineScope(
        context = SupervisorJob() + dispatcher + CoroutineExceptionHandler { context, exception ->
            // we can't be cancelled, so all exceptions are bugs
            logger.error(exception) {
                "IdentifyRateLimiter threw an exception in context $context, please report this, it should not happen"
            }
        }
    )

    override suspend fun consume(shardId: Int, events: SharedFlow<Event>) {
        require(shardId >= 0) { "shardId must be non-negative but was $shardId" }

        // if the coroutine that called consume() is cancelled, the CancellableContinuation makes sure the waiting is
        // stopped (the Gateway won't try to identify), so we don't need to hold the mutex and waste time for other
        // calls
        return suspendCancellableCoroutine { continuation ->
            val job = launchIdentifyWaiter(shardId, events, continuation)
            continuation.invokeOnCancellation { job.cancel() }
        }
    }

    private fun launchIdentifyWaiter(
        shardId: Int,
        events: SharedFlow<Event>,
        continuation: CancellableContinuation<Unit>,
    ) = scope.launch {
        val rateLimitKey = shardId % maxConcurrency
        val mutex = mutexesByRateLimitKey[rateLimitKey]
        val wasLocked = !mutex.tryLock()
        if (wasLocked) {
            logger.debug {
                "Waiting for other shard(s) with rate_limit_key $rateLimitKey to identify before identifying on " +
                    "shard $shardId"
            }
            mutex.lock()
        }
        try { // in case something terrible happens, ensure the mutex is unlocked
            // using a timeout so a broken gateway won't block its rate_limit_key for a long time
            val responseToIdentify = withTimeoutOrNull(IDENTIFY_TIMEOUT) {
                events.onSubscription { // onSubscription ensures we don't miss events
                    logger.debug {
                        "${
                            if (wasLocked) "Waited for other shard(s) with rate_limit_key $rateLimitKey to identify, i"
                            else "I"
                        }dentifying on shard $shardId with rate_limit_key $rateLimitKey..."
                    }
                    // notify gateway waiting in consume -> it will try to identify -> wait for event
                    continuation.resume(Unit)
                }.first { it is Ready || it is InvalidSession || it is Close }
            }
            logger.debug {
                when (responseToIdentify) {
                    null -> "Identifying on shard $shardId timed out"
                    is Ready -> "Identified on shard $shardId"
                    is InvalidSession -> "Identifying on shard $shardId failed, session could not be initialized"
                    is Close -> "Shard $shardId was stopped before it could identify"
                    else -> "Unexpected responseToIdentify on shard $shardId: $responseToIdentify"
                } + ", delaying $DELAY_AFTER_IDENTIFY before freeing up rate_limit_key $rateLimitKey"
            }
            delay(DELAY_AFTER_IDENTIFY) // delay before unlocking mutex to free up the current rateLimitKey
        } finally {
            mutex.unlock()
        }
    }

    override fun toString() = "IdentifyRateLimiter(maxConcurrency=$maxConcurrency, dispatcher=$dispatcher)"

    private companion object {
        // https://discord.com/developers/docs/topics/gateway#rate-limiting:
        // Apps also have a limit for concurrent Identify requests allowed per 5 seconds.
        // -> for each rate_limit_key: delay after identify
        private val DELAY_AFTER_IDENTIFY = 5.seconds

        private val IDENTIFY_TIMEOUT = DELAY_AFTER_IDENTIFY / 2
    }
}
