package com.gitlab.kordlib.core.cache

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.gateway.Close
import com.gitlab.kordlib.gateway.Gateway
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

/**
 * A bridge between [DataCache] and [Gateway] that automatically empties cache on disconnect.
 */
@FlowPreview

class CachingGateway(
        private val cache: DataCache,
        private val gateway: Gateway,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataCache by cache, Gateway by gateway, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + dispatcher

    init {
        gateway.events.filterIsInstance<Close>().onEach { removeKordData() }.launchIn(this)
    }
}