package com.gitlab.hopebaron.websocket.handler

import com.gitlab.hopebaron.websocket.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.coroutines.CoroutineContext

@FlowPreview
internal abstract class Handler(val flow: Flow<Event>, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job() + dispatcher

    init {
        launch {
            start()
        }
    }

    open fun start() {}

    inline fun <reified T> on(crossinline block: suspend (T) -> Unit) {
        launch {
            flow.filterIsInstance<T>().collect { block(it) }
        }
    }
}