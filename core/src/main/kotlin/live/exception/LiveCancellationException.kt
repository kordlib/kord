package dev.kord.core.live.exception

import dev.kord.core.event.Event
import java.util.concurrent.CancellationException

public class LiveCancellationException(public val event: Event, message: String? = null) :
    CancellationException(message)
