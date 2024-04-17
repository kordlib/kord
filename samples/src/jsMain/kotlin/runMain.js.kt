import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
actual fun runMain(block: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch { block() }
}
