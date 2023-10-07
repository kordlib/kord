package dev.kord.voice.handlers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import mu.KotlinLogging

private val logger = KotlinLogging.logger("[Interceptor]")

internal abstract class ConnectionEventHandler<Event>(
    val flow: Flow<Event>,
    val name: String,
) {
    open suspend fun start() {}

    protected inline fun <reified T> CoroutineScope.on(crossinline block: suspend (T) -> Unit) {
        flow.filterIsInstance<T>().onEach {
            try {
                block(it)
            } catch (exception: Exception) {
                logger.error(exception) { "[$name]" }
            }
        }.launchIn(this)
    }
}