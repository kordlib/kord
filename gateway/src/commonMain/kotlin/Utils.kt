package dev.kord.gateway

import mu.KLogger

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Binary compatibility, remove after deprecation cycle.", level = DeprecationLevel.ERROR)
@PublishedApi
internal fun KLogger.error(throwable: Throwable): Unit = error(throwable) { "" }
