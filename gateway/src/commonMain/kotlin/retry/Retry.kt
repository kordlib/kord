package dev.kord.gateway.retry

/**
 * A strategy for retrying after after a failed action.
 */
public interface Retry {
    /**
     * Whether this strategy has any more retries left.
     */
    public val hasNext: Boolean

    /**
     * Resets the underlying retry counter if this Retry uses an maximum for consecutive [retry] invocations.
     * This should be called after a successful [retry].
     */
    public fun reset()

    /**
     * Suspends for a certain amount of time.
     */
    public suspend fun retry()
}
