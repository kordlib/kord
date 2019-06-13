package com.gitlab.hopebaron.websocket.retry

/**
 * A strategy for retrying after after a failed action.
 */
interface Retry {
    /**
     * Whether this strategy has any more retries left.
     */
    val hasNext: Boolean

    /**
     * Resets the underlying retry counter if this Retry uses an maximum for consecutive [retry] invocations.
     * This should be called after a successful [retry].
     */
    fun reset()

    /**
     * Suspends for a certain amount of time.
     */
    suspend fun retry()
}
