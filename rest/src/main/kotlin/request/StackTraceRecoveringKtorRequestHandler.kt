package dev.kord.rest.request


/**
 * Extension of [KtorRequestHandler] which tries to recover stack trace information lost through Ktor's
 * [io.ktor.util.pipeline.SuspendFunctionGun].
 *
 * This is done by creating a [ContextException] to capture the stack trace up until the point just before
 * [KtorRequestHandler.handle] gets called, then if that call throws any type of [Throwable] the [ContextException] gets
 * thrown instead (See [ContextException.cause])
 *
 * @see ContextException
 * @see withStackTraceRecovery
 */
public class StackTraceRecoveringKtorRequestHandler(private val delegate: KtorRequestHandler) :
    RequestHandler by delegate {

    /**
     * @throws ContextException if any exception occurs (this is also the only exception which can be thrown)
     * @see KtorRequestHandler.handle
     */
    override suspend fun <B : Any, R> handle(request: Request<B, R>): R {
        val stacktrace = ContextException()

        return try {
            delegate.handle(request)
        } catch (e: Exception) {
            throw stacktrace.apply {
                sanitizeStackTrace()
                initCause(e)
            }
        }
    }
}

/**
 * Exception used to save the current stack trace before executing a request.
 *
 * @see StackTraceRecoveringKtorRequestHandler
 */
public class ContextException internal constructor() : RuntimeException() {

    internal fun sanitizeStackTrace() {
        // Remove artifacts of stack trace capturing
        // This is the stack trace element is the creation of the ContextException
        // at dev.kord.rest.request.StackTraceRecoveringKtorRequestHandler.handle(StackTraceRecoveringKtorRequestHandler.kt:23)
        stackTrace = stackTrace.copyOfRange(1, stackTrace.size)
    }
}

/**
 * Returns a new [RequestHandler] with stack trace recovery enabled.
 *
 * @see StackTraceRecoveringKtorRequestHandler
 */
public fun KtorRequestHandler.withStackTraceRecovery(): StackTraceRecoveringKtorRequestHandler =
    StackTraceRecoveringKtorRequestHandler(this)
