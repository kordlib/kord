package dev.kord.gateway

import mu.KLogger

@Deprecated("Binary compatibility, remove after deprecation cycle.", level = DeprecationLevel.HIDDEN)
@PublishedApi
internal fun KLogger.error(throwable: Throwable): Unit = error(throwable) { "" }
