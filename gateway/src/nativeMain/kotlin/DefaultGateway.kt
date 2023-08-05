package dev.kord.gateway

import kotlin.experimental.ExperimentalNativeApi

// TODO: Timeout
internal actual fun Throwable.isTimeout() = false

@OptIn(ExperimentalNativeApi::class)
internal actual val os: String get() = Platform.osFamily.name
