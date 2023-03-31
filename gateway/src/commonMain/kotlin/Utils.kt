package dev.kord.gateway

import mu.KLogger

@PublishedApi
internal fun KLogger.error(throwable: Throwable): Unit = error(throwable) { "" }
