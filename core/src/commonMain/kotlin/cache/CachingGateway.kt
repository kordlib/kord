package dev.kord.core.cache

import dev.kord.cache.api.DataCache
import dev.kord.gateway.Close
import dev.kord.gateway.Gateway
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

/**
 * A bridge between [DataCache] and [Gateway] that automatically empties cache on disconnect.
 */
public class CachingGateway(
    private val cache: DataCache,
    private val gateway: Gateway,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : DataCache by cache, Gateway by gateway, CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + dispatcher

    init {
        gateway.events.filterIsInstance<Close>().onEach { removeKordData() }.launchIn(this)
    }

    override fun toString(): String {
        return "CachingGateway(cache=$cache, gateway=$gateway)"
    }
}
