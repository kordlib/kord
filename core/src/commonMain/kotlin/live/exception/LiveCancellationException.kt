package dev.kord.core.live.exception

import dev.kord.core.event.Event
import kotlinx.coroutines.CancellationException

public class LiveCancellationException(public val event: Event, message: String? = null) :
    CancellationException(message)
