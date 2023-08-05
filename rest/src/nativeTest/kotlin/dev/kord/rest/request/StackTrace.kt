package dev.kord.rest.request

import kotlin.test.assertEquals

actual typealias StackTraceElement = String

//kotlin.Exception
//at 0   ???                                 7ff68473da75       kfun:kotlin.Throwable#<init>(kotlin.String?){} + 117
//at 1   ???                                 7ff684f9ee89       kfun:dev.kord.rest.request.RecoveredStackTrace#<init>(){} + 89
//at 2   ???                                 7ff684f9e939       kfun:dev.kord.rest.request.StackTraceRecoveringKtorRequestHandler.$handleCOROUTINE$23#invokeSuspend(kotlin.Result<kotlin.Any?>){}kotlin.Any? + 681
//at 3   ???                                 7ff684f9ed3c       kfun:dev.kord.rest.request.StackTraceRecoveringKtorRequestHandler#handle(dev.kord.rest.request.Request<0:0,0:1>){0§<kotlin.Any>;1§<kotlin.Any?>}0:1 + 300
//at 4   ???                                 7ff684fbd4c4       kfun:dev.kord.rest.request.StackTraceRecoveryTest.$test stack trace recovery$lambda$1COROUTINE$15.invokeSuspend#internal + 2740
//-->at 5   ???                                 7ff684fbdeca       kfun:dev.kord.rest.request.StackTraceRecoveryTest.$test stack trace<--
actual fun currentThreadStackTrace(): StackTraceElement =
    Exception().stackTraceToString().lineSequence().filterNot(String::isBlank).drop(5).first().trim()
        .substringAfter("???")

internal actual fun RecoveredStackTrace.validate(expected: StackTraceElement) {
    // The first few lines are artifacts from coroutines which are not present in expected
    val actual = stackTraceToString().lineSequence().drop(6).first().trim()
        .substringAfter("???") // index is off at call site
    assertEquals(expected, actual)
}
