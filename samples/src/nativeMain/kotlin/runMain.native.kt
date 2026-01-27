import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

actual fun runMain(block: suspend CoroutineScope.() -> Unit) {
    runBlocking { block() }
}