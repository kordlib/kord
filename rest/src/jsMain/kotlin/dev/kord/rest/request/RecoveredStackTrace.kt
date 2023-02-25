package dev.kord.rest.request

internal actual fun RecoveredStackTrace.sanitizeStackTrace() {
    // Remove artifacts of stack trace capturing
    // ContextException:
    // REMOVE: at 0.doResume_0(/home/mik/IdeaProjects/ktor-suspend-function-gun-tests/src/commonMain/kotlin/Request.kt:12)
    // REMOVE: at <global>.doCatching(/home/mik/IdeaProjects/ktor-suspend-function-gun-tests/src/commonMain/kotlin/Request.kt:11)
    // at _no_name_provided__304.doResume_0(/home/mik/IdeaProjects/ktor-suspend-function-gun-tests/src/commonTest/kotlin/Test.kt:10)
    // at _no_name_provided__304.invoke_29q9u6(/home/mik/IdeaProjects/ktor-suspend-function-gun-tests/src/commonTest/kotlin/Test.kt:9)
    val dynamic = asDynamic()
    val stack = dynamic.stack as String? ?: ""
    stackTraceToString()
    dynamic.stack = stack.lines().toMutableList().apply {
        repeat(2) {
            removeAt(1)
        }
    }.joinToString("\n")
}
