package dev.kord.voice.handlers

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger("[Interceptor]")

internal abstract class EventHandler<Event>(
    val flow: Flow<Event>,
    val name: String,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : CoroutineScope {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + dispatcher + CoroutineName("Voice Connection Event Handler [$name]")

    init {
        launch {
            start()
        }
    }

    open fun start() {}

    inline fun <reified T> on(crossinline block: suspend (T) -> Unit) {
        flow.filterIsInstance<T>().onEach {
            try {
                block(it)
            } catch (exception: Exception) {
                logger.error(exception) { "[$name]" }
            }
        }.launchIn(this)
    }
}