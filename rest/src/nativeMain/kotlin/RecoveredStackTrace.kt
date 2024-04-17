package dev.kord.rest.request

// You cannot really modify stack traces in Native :(
internal actual fun RecoveredStackTrace.sanitizeStackTrace() = Unit
