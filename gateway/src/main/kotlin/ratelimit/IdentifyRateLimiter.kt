package dev.kord.gateway.ratelimit

import dev.kord.gateway.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlinx.atomicfu.loop
import kotlinx.atomicfu.update
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.select
import mu.KotlinLogging
import kotlin.DeprecationLevel.ERROR
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

    private class IdentifyRequest(
        @JvmField val shardId: Int,
        @JvmField val events: SharedFlow<Event>,
        private val permission: CompletableDeferred<Unit>,
    ) {
        fun allow() = permission.complete(Unit)
    }

    private val IdentifyRequest.rateLimitKey get() = shardId % maxConcurrency

    // doesn't need onUndeliveredElement for rejecting requests, we don't close or cancel the channel, receiving can't
    // be cancelled (running in GlobalScope), only send can be cancelled, which is ok because permission isn't needed in
    // that case
    private val channel = Channel<IdentifyRequest>(capacity = maxConcurrency)

    /**
     * Can be
     * - [NOT_RUNNING]: no coroutine ([launchRateLimiterCoroutine]) is running, or it is about to stop and will no
     *   longer process [IdentifyRequest]s
     * - [RUNNING_WITH_NO_CONSUMERS]: the coroutine is running and ready to process [IdentifyRequest]s but there are no
     *   consumers that sent a request
     * - unsigned number of consumers: number of concurrent [consume] invocations that wait for their [IdentifyRequest]
     *   to be processed (min [ONE_CONSUMER], max [MAX_CONSUMERS])
     */
    private val state = atomic(initial = NOT_RUNNING)

    private fun getOldStateAndIncrementConsumers() = state.getAndUpdate { current ->
        when (current) {
            NOT_RUNNING, RUNNING_WITH_NO_CONSUMERS -> ONE_CONSUMER // we are the first consumer
            MAX_CONSUMERS -> error(
                "Too many concurrent identify attempts, overflow happened. There are already ${current.toUInt()} " +
                        "other consume() invocations waiting. This is most likely a bug."
            )
            else -> current + 1 // increment number of consumers
        }
    }

    private fun decrementConsumers() = state.update { current ->
        when (current) {
            NOT_RUNNING -> error("Should be running but was NOT_RUNNING")
            RUNNING_WITH_NO_CONSUMERS -> error("Should have consumers but was RUNNING_WITH_NO_CONSUMERS")
            ONE_CONSUMER -> RUNNING_WITH_NO_CONSUMERS // we were the last consumer
            else -> current - 1 // decrement number of consumers
        }
    }

    private fun stopIfHasNoConsumers(): Boolean = state.loop { current ->
        when (current) {
            NOT_RUNNING -> error("Should be running but was NOT_RUNNING")
            RUNNING_WITH_NO_CONSUMERS -> // no new requests in sight -> try to stop
                if (state.compareAndSet(expect = current, update = NOT_RUNNING)) return true
            else -> return false // don't change number of consumers
        }
    }


    override suspend fun consume(shardId: Int, events: SharedFlow<Event>) {
        require(shardId >= 0) { "shardId must be non-negative but was $shardId" }

        val oldState = getOldStateAndIncrementConsumers()
        try {
            if (oldState == NOT_RUNNING) launchRateLimiterCoroutine()

            val permission = CompletableDeferred<Unit>()
            channel.send(IdentifyRequest(shardId, events, permission))
            permission.await()
        } finally {
            decrementConsumers()
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
        GlobalScope.launch(context = dispatcher + ExceptionLogger) {

            // only read/written from sequential loop, not from launched concurrent coroutines
            // request.rateLimitKey is always in the range 0..<maxConcurrency
            val identifyWaiters = arrayOfNulls<Job>(size = maxConcurrency)

            while (true) {
                val batch = receiveSortedBatchOfRequestsOrNull() ?: when {
                    identifyWaiters.any { it != null && !it.isCompleted } -> continue // keep receiving while waiting
                    stopIfHasNoConsumers() -> return@launch // no consumers and no waiters -> stop (only exit point)
                    else -> continue // has consumers -> there will be new requests soon
                }

                for (request in batch) {
                    val key = request.rateLimitKey
                    val previousWaiter = identifyWaiters[key]
                    identifyWaiters[key] = launchIdentifyWaiter(previousWaiter, request)
                }
            }
        }
    }

    /** Returns `null` on [RECEIVE_TIMEOUT]. */
    private suspend fun receiveSortedBatchOfRequestsOrNull(): List<IdentifyRequest>? {

        // first receive suspends until we get a request or time out
        // using select instead of withTimeoutOrNull here, so we can't remove the request from the channel on timeout
        val firstRequest = select<IdentifyRequest?> {
            channel.onReceive { it }
            @OptIn(ExperimentalCoroutinesApi::class) onTimeout(RECEIVE_TIMEOUT) { null }
        } ?: return null

        yield() // give other requests the chance to arrive if they were sent at the same time

        return buildList {
            add(firstRequest)

            do { // now we receive other requests that are immediately available
                val result = channel.tryReceive().onSuccess(::add)
            } while (result.isSuccess)

            sortWith(ShardIdComparator) // sort requests in this batch
        }
    }

    private fun CoroutineScope.launchIdentifyWaiter(previousWaiter: Job?, request: IdentifyRequest) = launch {
        if (previousWaiter != null) {
            logger.debug {
                "Waiting for other shard(s) with rate_limit_key ${request.rateLimitKey} to identify " +
                        "before identifying on shard ${request.shardId}"
            }
            previousWaiter.join()
        }

        // using a timeout so a broken gateway won't block its rate_limit_key for a long time
        val responseToIdentify = withTimeoutOrNull(IDENTIFY_TIMEOUT) {
            request.events
                .onSubscription { // onSubscription ensures we don't miss events
                    logger.debug {
                        "${
                            if (previousWaiter != null)
                                "Waited for other shard(s) with rate_limit_key ${request.rateLimitKey} to identify, i"
                            else "I"
                        }dentifying on shard ${request.shardId} with rate_limit_key ${request.rateLimitKey}..."
                    }
                    request.allow() // notify gateway waiting in consume -> it will try to identify -> wait for event
                }
                .first { it is Ready || it is InvalidSession || it is Close }
        }

        logger.debug {
            when (responseToIdentify) {
                null -> "Identifying on shard ${request.shardId} timed out"
                is Ready -> "Identified on shard ${request.shardId}"
                is InvalidSession -> "Identifying on shard ${request.shardId} failed, session could not be initialized"
                is Close -> "Shard ${request.shardId} was stopped before it could identify"
                else -> "Unexpected responseToIdentify on shard ${request.shardId}: $responseToIdentify"
            } + ", delaying $DELAY_AFTER_IDENTIFY before freeing up rate_limit_key ${request.rateLimitKey}"
        }

        // next waiter for the current rate_limit_key has to wait for this delay before it can identify
        delay(DELAY_AFTER_IDENTIFY)
    }


    override fun toString() = "IdentifyRateLimiter(maxConcurrency=$maxConcurrency, dispatcher=$dispatcher)"


    private companion object {
        // https://discord.com/developers/docs/topics/gateway#rate-limiting:
        // Apps also have a limit for concurrent Identify requests allowed per 5 seconds.
        // -> for each rate_limit_key: delay after identify
        private val DELAY_AFTER_IDENTIFY = 5.seconds

        private val RECEIVE_TIMEOUT = (2 * DELAY_AFTER_IDENTIFY).inWholeMilliseconds
        private val IDENTIFY_TIMEOUT = DELAY_AFTER_IDENTIFY / 2

        // states
        private const val MAX_CONSUMERS = -2 // interpreted as UInt.MAX_VALUE - 1u
        private const val NOT_RUNNING = -1
        private const val RUNNING_WITH_NO_CONSUMERS = 0
        private const val ONE_CONSUMER = 1

        private val ShardIdComparator = Comparator<IdentifyRequest> { r1, r2 -> r1.shardId.compareTo(r2.shardId) }

        private val ExceptionLogger = CoroutineExceptionHandler { context, exception ->
            // we can't be cancelled (GlobalScope) and we never close the channel, so all exceptions are bugs
            logger.error(
                "IdentifyRateLimiter threw an exception in context $context, please report this, it should not happen",
                exception,
            )
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

    override suspend fun consume(shardId: Int, events: SharedFlow<Event>) {
        // check unused param to fulfil documented contract
        require(shardId >= 0) { "shardId must be non-negative but was $shardId" }
        commonRateLimiter.consume()
    }

    override fun toString() = "IdentifyRateLimiterFromCommonRateLimiter(commonRateLimiter=$commonRateLimiter)"
}
