package dev.kord.rest.request

import kotlin.test.assertEquals

actual typealias StackTraceElement = java.lang.StackTraceElement

// [0]: java.lang.Thread.getStackTrace(Thread.java)
@Suppress("NOTHING_TO_INLINE") // inlining produces the actual stacktrace
actual inline fun currentThreadStackTrace(): StackTraceElement = Thread.currentThread().stackTrace[1]

internal actual fun RecoveredStackTrace.validate(expected: StackTraceElement) {
    // at dev.kord.rest.request.StackTraceRecoveryTest$test stack trace recovery$1.invokeSuspend(StackTraceRecoveryTest.kt:39)
    with(stackTrace.first()) {
        assertEquals(expected.className, className)
        assertEquals(expected.fileName, fileName)
        // -11 because there is a discrepancy due to coroutines
        assertEquals(expected.lineNumber - 11, lineNumber)
        assertEquals(expected.methodName, methodName)
    }
}
