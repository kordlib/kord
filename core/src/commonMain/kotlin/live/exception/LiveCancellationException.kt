package dev.kord.core.live.exception

import dev.kord.core.event.Event
import kotlinx.coroutines.CancellationException

/**
 * Thrown when a Live Event was cancelled.
 *
 * @property message The detail message
 */
public class LiveCancellationException(public val event: Event, message: String? = null) :
    CancellationException(message)
