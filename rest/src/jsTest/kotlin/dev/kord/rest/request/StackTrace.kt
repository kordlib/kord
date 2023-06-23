package dev.kord.rest.request

import kotlin.test.assertEquals

actual typealias StackTraceElement = String

//[0]: at $handleCOROUTINE$6.doResume_5yljmg_k$ (C:\Users\micha\IdeaProjects\kord\rest\src\commonMain\kotlin\request\KtorRequestHandler.kt:64:28)
//[1]: at currentThreadStackTrace (C:\Users\micha\IdeaProjects\kord\rest\src\jsTest\kotlin\dev\kord\rest\request\StackTrace.kt:5:59)
//[2]: at $executeCOROUTINE$0.CoroutineImpl.resumeWith_7onugl_k$ (C:\Users\micha\IdeaProjects\kord\rest\build\compileSync\js\test\testDevelopmentExecutable\kotlin\commonMainSources\libraries\stdlib\src\kotlin\util\Standard.kt:55:40)
actual fun currentThreadStackTrace(): StackTraceElement =
    Exception().stackTraceToString().lineSequence().drop(3).first().trim()

internal actual fun RecoveredStackTrace.validate(expected: StackTraceElement) {
    // The first two lines are artifacts from coroutines which are not present in expected
    val actual = stackTraceToString().lineSequence().drop(2).first().trim()
    assertEquals(expected, actual)
}
