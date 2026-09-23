package dev.kord.voice.handlers

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private val logger = KotlinLogging.logger("[Voice Handler]")

internal abstract class ConnectionEventHandler<Event>(
    val flow: Flow<Event>,
    val name: String,
) {
    open suspend fun start() {}

    protected inline fun <reified T> CoroutineScope.on(crossinline block: suspend (T) -> Unit) {
        flow.filterIsInstance<T>().onEach {
            try {
                block(it)
            } catch (e: CancellationException) {
                throw e
            } catch (exception: Exception) {
                logger.error(exception) { "[$name]" }
            }
        }.launchIn(this)
    }
}
