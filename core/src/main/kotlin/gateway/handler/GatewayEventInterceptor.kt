package dev.kord.core.gateway.handler

import dev.kord.core.Kord
import dev.kord.core.gateway.ShardEvent
import dev.kord.core.event.Event as CoreEvent

/**
 * Instances of this type are used to convert [gateway events][dev.kord.gateway.Event] to
 * [core events][dev.kord.core.event.Event].
 */
public interface GatewayEventInterceptor {

    /**
     * Converts a [gateway event][dev.kord.gateway.Event] (in the form of a [ShardEvent]) to a
     * [core event][dev.kord.core.event.Event].
     *
     * This might also have side effects like updating the [cache][Kord.cache].
     */
    public suspend fun handle(event: ShardEvent, kord: Kord): CoreEvent?


    public companion object {
        private object None : GatewayEventInterceptor {
            override suspend fun handle(event: ShardEvent, kord: Kord) = null
        }

        /**
         * Returns a [GatewayEventInterceptor] with no-op behavior.
         *
         * [handle] will always return `null`.
         */
        public fun none(): GatewayEventInterceptor = None
    }
}
