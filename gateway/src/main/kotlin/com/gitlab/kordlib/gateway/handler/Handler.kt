package com.gitlab.kordlib.gateway.handler

import com.gitlab.kordlib.gateway.Close
import com.gitlab.kordlib.gateway.Event
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger("[Handler]")

internal abstract class Handler(val flow: Flow<Event>, val name: String, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + dispatcher

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