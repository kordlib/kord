package dev.kord.rest.request

/**
 * Extension of [KtorRequestHandler] which tries to recover stack trace information lost through Ktor's
 * [io.ktor.util.pipeline.SuspendFunctionGun].
 *
 * This is done by creating a [RecoveredStackTrace] to capture the stack trace up until the point just before
 * [KtorRequestHandler.handle] gets called, then if that call throws any type of [Throwable] the [RecoveredStackTrace]
 * gets added to the original [Throwable] as a [suppressed exception][addSuppressed] and the original [Throwable] is
 * rethrown.
 *
 * @see withStackTraceRecovery
 */
public class StackTraceRecoveringKtorRequestHandler(private val delegate: KtorRequestHandler) :
    RequestHandler by delegate {

    /** @see KtorRequestHandler.handle */
    override suspend fun <B : Any, R> handle(request: Request<B, R>): R {
        val recoveredStackTrace = RecoveredStackTrace()

        return try {
            delegate.handle(request)
        } catch (e: Throwable) {
            recoveredStackTrace.sanitizeStackTrace()
            e.addSuppressed(recoveredStackTrace)
            throw e
        }
    }
}

/** A [Throwable] used to save the current stack trace before executing a request. */
internal class RecoveredStackTrace : Throwable("This is the recovered stack trace:") {

    fun sanitizeStackTrace() {
        // Remove artifacts of stack trace capturing.
        // The first stack trace element is the creation of the RecoveredStackTrace:
        // at dev.kord.rest.request.StackTraceRecoveringKtorRequestHandler.handle(StackTraceRecoveringKtorRequestHandler.kt:21)
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
