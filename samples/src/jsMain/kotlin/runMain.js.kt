import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

actual fun runMain(block: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch { block() }
}