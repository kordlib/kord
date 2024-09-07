package dev.kord.gateway

import mu.KLogger

@Deprecated(
    "Kept for binary compatibility, this declaration will be removed in 0.16.0.",
    level = DeprecationLevel.HIDDEN,
)
@PublishedApi
internal fun KLogger.error(throwable: Throwable): Unit = error(throwable) { "" }
