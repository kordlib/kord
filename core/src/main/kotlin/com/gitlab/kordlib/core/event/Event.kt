package com.gitlab.kordlib.core.event

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.gateway.Gateway
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

interface Event : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = kord.coroutineContext

    /**
     * The Gateway that spawned this event.
     */
    val gateway: Gateway get() = kord.gateway.gateways.getValue(shard)

    val kord: Kord

    /**
     * The shard number of the [gateway] that spawned this event.
     */
    val shard: Int

}