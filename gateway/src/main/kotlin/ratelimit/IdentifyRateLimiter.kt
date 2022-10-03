package dev.kord.gateway.ratelimit

import dev.kord.gateway.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlinx.atomicfu.update
import kotlinx.atomicfu.updateAndGet
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineStart.UNDISPATCHED
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.selects.select
import mu.KotlinLogging
import kotlin.DeprecationLevel.ERROR
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.time.Duration.Companion.seconds
import kotlin.time.times
import dev.kord.common.ratelimit.IntervalRateLimiter as CommonIntervalRateLimiter
import dev.kord.common.ratelimit.RateLimiter as CommonRateLimiter

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
     * Identifying is considered complete once [Ready] or [Close] is observed in [events].
     *
     * @throws IllegalArgumentException if [shardId] is negative.
     */
    public suspend fun consume(shardId: Int, events: Flow<Event>)
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

    private class IdentifyRequest(
        val shardId: Int,
        val events: Flow<Event>,
        private val permission: CompletableDeferred<Unit>,
    ) {
        fun allow() = permission.complete(Unit)
        fun reject(cause: Throwable) = permission.completeExceptionally(cause)
    }

    // doesn't need onUndeliveredElement for rejecting requests, we don't close or cancel the channel, receiving can't
    // be cancelled (running in GlobalScope), only send can be cancelled, which is ok because permission isn't needed in
    // that case
    private val channel = Channel<IdentifyRequest>(capacity = maxConcurrency)

    /**
     * Can be
     * - [NOT_RUNNING]: no coroutine ([launchRateLimiterCoroutine]) is running, or it is about to stop and will no
     *   longer process [IdentifyRequest]s
     * - [RUNNING_WITH_NO_WAITERS]: the coroutine is running and ready to process [IdentifyRequest]s but there are no
     *   waiters that sent a request
     * - unsigned number of waiters: number of concurrent [consume] invocations that wait for their [IdentifyRequest] to
     *   be processed (min [ONE_WAITER], max [MAX_WAITERS])
     */
    private val state = atomic(initial = NOT_RUNNING)

    private fun getOldStateAndIncrementWaiters() = state.getAndUpdate { current ->
        when (current) {
            NOT_RUNNING, RUNNING_WITH_NO_WAITERS -> ONE_WAITER // we are the first waiter
            MAX_WAITERS -> error(
                "Too many concurrent identify attempts, overflow happened. There are already ${current.toUInt()} " +
                        "other consume() invocations waiting. This is most likely a bug."
            )
            else -> current + 1 // increment number of waiters
        }
    }

    private fun decrementWaiters() = state.update { current ->
        when (current) {
            NOT_RUNNING -> error("Should be running but was NOT_RUNNING")
            RUNNING_WITH_NO_WAITERS -> error("Should have waiters but was RUNNING_WITH_NO_WAITERS")
            ONE_WAITER -> RUNNING_WITH_NO_WAITERS // we were the last waiter
            else -> current - 1 // decrement number of waiters
        }
    }

    private fun checkWaitersAndGetNewState() = state.updateAndGet { current ->
        when (current) {
            NOT_RUNNING -> error("We are running, so we can't observe NOT_RUNNING")
            RUNNING_WITH_NO_WAITERS -> NOT_RUNNING // no new requests in sight
            else -> current // don't change number of waiters
        }
    }


    override suspend fun consume(shardId: Int, events: Flow<Event>) {
        require(shardId >= 0) { "shardId must be non-negative but was $shardId" }

        val oldState = getOldStateAndIncrementWaiters()
        try {
            if (oldState == NOT_RUNNING) launchRateLimiterCoroutine()

            val permission = CompletableDeferred<Unit>()
            channel.send(IdentifyRequest(shardId, events, permission))
            permission.await()
        } finally {
            decrementWaiters()
        }
    }


    private fun launchRateLimiterCoroutine() {
        // GlobalScope is ok here:
        // - only one coroutine is launched at a time:
        //   previously running one will allow next one to start by setting state to NOT_RUNNING just before exiting
        // - the coroutine will time out eventually when no identify requests are sent, a new one will be launched on
        //   demand which will then time out again etc.
        // => no leaks
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch(dispatcher) {
            do {
                // we can't be cancelled (GlobalScope) and we never close the channel, so all exceptions are bugs
                val runAgainAfterTimeout = retryOnUnexpectedException {
                    runRateLimiterAndGetWhetherShouldRunAgainAfterTimeout()
                }
            } while (runAgainAfterTimeout)
        }
    }


    // only returns normally on timeout
    private suspend fun runRateLimiterAndGetWhetherShouldRunAgainAfterTimeout(): Boolean {
        while (true) {
            // will produce a list of at least one identify request
            val requests = buildList {

                // reject all requests we received so far when an exception is thrown somewhere
                rethrowExceptionAndReject(requests = this@buildList) {

                    // first receive suspends until we get a request or timeout
                    // using select instead of withTimeoutOrNull here, so we don't remove the request from the channel
                    // on timeout
                    val request = select<IdentifyRequest?> {
                        channel.onReceive { it }

                        @OptIn(ExperimentalCoroutinesApi::class)
                        onTimeout(TIMEOUT) { null }
                    }

                    if (request == null) { // timeout
                        val runAgainAfterTimeout = when (checkWaitersAndGetNewState()) {
                            NOT_RUNNING -> false
                            RUNNING_WITH_NO_WAITERS -> error("Can't be RUNNING_WITH_NO_WAITERS after checking waiters")
                            else -> true // there are waiters, next request will be sent soon -> won't time out again
                        }
                        return runAgainAfterTimeout
                    }

                    this@buildList.add(request)

                    // now we collect other requests that are immediately available
                    do {
                        yield() // give elements the chance to arrive if they were sent at the same time
                        val result = channel.tryReceive().onSuccess(this@buildList::add)
                    } while (result.isSuccess)
                }
            }

            rethrowExceptionAndReject(requests) { handle(requests) }

            delay(DELAY_AFTER_BUCKET_OF_CONCURRENT_IDENTIFIES)
        }
    }

    private suspend fun handle(requests: List<IdentifyRequest>) = coroutineScope {
        // mutability is ok here, not read/written from launched concurrent coroutines
        var previousRateLimitKey = -1
        val identifiedListeners = mutableListOf<Pair<Int, Job>>()

        for (request in requests.sortedBy { it.shardId }) {
            val rateLimitKey = request.shardId % maxConcurrency

            if (rateLimitKey <= previousRateLimitKey) {
                val (shardIds, jobs) = identifiedListeners.unzip()
                logger.debug {
                    "Waiting for shards $shardIds to identify before identifying on shard ${request.shardId} " +
                            "(rate_limit_key $rateLimitKey) and above..."
                }

                jobs.joinAll()
                identifiedListeners.clear()

                delay(DELAY_AFTER_BUCKET_OF_CONCURRENT_IDENTIFIES)
            }

            // make sure to not miss events by executing until first suspension point before giving permission
            identifiedListeners += request.shardId to launch(start = UNDISPATCHED) {
                val event = request.events.first { it is Ready || it is Close }
                logger.debug {
                    "${
                        if (event is Ready) "Identified on shard ${request.shardId}"
                        else "Identifying on shard ${request.shardId} failed"
                    }, freeing up rate_limit_key $rateLimitKey"
                }
            }

            logger.debug { "Identifying on shard ${request.shardId} (rate_limit_key $rateLimitKey)..." }
            // notify gateway waiting in consume -> it will try to identify -> wait for event
            request.allow()

            previousRateLimitKey = rateLimitKey
        }
    }


    override fun toString() = "IdentifyRateLimiter(maxConcurrency=$maxConcurrency, dispatcher=$dispatcher)"


    private companion object {
        // https://discord.com/developers/docs/topics/gateway#rate-limiting:
        // Apps also have a limit for concurrent Identify requests allowed per 5 seconds.
        // -> delay after each bucket
        private val DELAY_AFTER_BUCKET_OF_CONCURRENT_IDENTIFIES = 5.seconds

        private val TIMEOUT = (2 * DELAY_AFTER_BUCKET_OF_CONCURRENT_IDENTIFIES).inWholeMilliseconds

        // states
        private const val MAX_WAITERS = -2 // interpreted as UInt.MAX_VALUE - 1u
        private const val NOT_RUNNING = -1
        private const val RUNNING_WITH_NO_WAITERS = 0
        private const val ONE_WAITER = 1


        private inline fun <R> retryOnUnexpectedException(block: () -> R): R {
            contract { callsInPlace(block, AT_LEAST_ONCE) }

            while (true) {
                try {
                    return block()
                } catch (t: Throwable) {
                    try {
                        logger.error(
                            "IdentifyRateLimiter threw an exception, please report this, it should not happen",
                            t,
                        )
                    } catch (_: Throwable) {
                        // logger failed, this is bad
                    }
                }
            }
        }

        private inline fun <R> rethrowExceptionAndReject(requests: Iterable<IdentifyRequest>, block: () -> R): R {
            contract { callsInPlace(block, EXACTLY_ONCE) }

            try {
                return block()
            } catch (t: Throwable) {
                requests.forEach { it.reject(cause = t) }
                throw t
            }
        }
    }
}


@Deprecated("For migration purposes, remove once DefaultGatewayData.oldIdentifyRateLimiter is removed", level = ERROR)
internal class IdentifyRateLimiterFromCommonRateLimiter(
    val commonRateLimiter: CommonRateLimiter,
) : IdentifyRateLimiter {

    override val maxConcurrency
        get() = if (commonRateLimiter is CommonIntervalRateLimiter && commonRateLimiter.interval == 5.seconds) {
            commonRateLimiter.limit
        } else throw UnsupportedOperationException()

    override suspend fun consume(shardId: Int, events: Flow<Event>) {
        // check unused param to fulfil documented contract
        require(shardId >= 0) { "shardId must be non-negative but was $shardId" }
        commonRateLimiter.consume()
    }

    override fun toString() = "IdentifyRateLimiterFromCommonRateLimiter(commonRateLimiter=$commonRateLimiter)"
}
