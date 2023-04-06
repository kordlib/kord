package dev.kord.rest.request

internal actual fun RecoveredStackTrace.sanitizeStackTrace() {
    // Remove artifacts of stack trace capturing.
    // The first stack trace element is the creation of the RecoveredStackTrace:
    // at dev.kord.rest.request.StackTraceRecoveringKtorRequestHandler.handle(StackTraceRecoveringKtorRequestHandler.kt:19)
    (this as Throwable).stackTrace = stackTrace.copyOfRange(1, stackTrace.size)
}
