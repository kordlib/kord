package dev.kord.gateway

import java.nio.channels.UnresolvedAddressException

internal actual fun Throwable.isTimeout() = this is UnresolvedAddressException
internal actual val os: String get() = System.getProperty("os.name")
