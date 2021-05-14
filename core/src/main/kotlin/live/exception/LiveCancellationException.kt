package dev.kord.core.live.exception

import dev.kord.core.event.Event
import java.util.concurrent.CancellationException

class LiveCancellationException(val event: Event, reason: String? = null) : CancellationException(reason)