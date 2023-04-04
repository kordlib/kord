package dev.kord.rest.request

import kotlin.test.assertEquals

actual typealias StackTraceElement = java.lang.StackTraceElement

// [0]: java.lang.Thread.getStackTrace(Thread.java)
actual fun currentThreadStackTrace(): StackTraceElement = Thread.currentThread().stackTrace[1]

internal actual fun RecoveredStackTrace.validate(expected: StackTraceElement) {
    // at dev.kord.rest.request.StackTraceRecoveryTest$test stack trace recovery$1.invokeSuspend(StackTraceRecoveryTest.kt:39)
    with(stackTrace.first()) {
        assertEquals(expected.className, className)
        assertEquals(expected.fileName, fileName)
        assertEquals(expected.lineNumber + 2, lineNumber) // +2 because capture is two lines deeper
        assertEquals(expected.methodName, methodName)
    }
}
