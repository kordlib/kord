package dev.kord.gateway.handler

import dev.kord.gateway.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger("[Handler]")

internal abstract class Handler(
    val flow: Flow<Event>,
    val name: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + dispatcher

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