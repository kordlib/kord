package com.gitlab.kordlib.gateway.handler

import com.gitlab.kordlib.gateway.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

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
        flow
                .filterIsInstance<T>()
                .onEach { block(it) }
                .launchIn(this)
    }

}