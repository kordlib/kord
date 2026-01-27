import kotlinx.coroutines.CoroutineScope

expect fun runMain(block: suspend CoroutineScope.() -> Unit)
