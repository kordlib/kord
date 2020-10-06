package com.gitlab.kordlib.core.builder.kord

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.common.annotation.KordExperimental
import com.gitlab.kordlib.core.ClientResources
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.exception.KordInitializationException
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.Intents
import com.gitlab.kordlib.rest.ratelimit.ExclusionRequestRateLimiter
import com.gitlab.kordlib.rest.request.KtorRequestHandler
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.service.RestClient
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel

@KordExperimental
class KordRestOnlyBuilder(val token: String) {

    private var handlerBuilder: (resources: ClientResources) -> RequestHandler =
            { KtorRequestHandler(it.httpClient, ExclusionRequestRateLimiter()) }

    /**
     * The [CoroutineDispatcher] kord uses to launch suspending tasks. [Dispatchers.Default] by default.
     */
    var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    /**
     * The client used for building [Gateways][Gateway] and [RequestHandlers][RequestHandler]. A default implementation
     * will be used when not set.
     */
    var httpClient: HttpClient? = null

    /**
     * Configures the [RequestHandler] for the [RestClient].
     *
     * ```
     * Kord(token) {
     *     { resources -> KtorRequestHandler(resources.httpClient, ExclusionRequestRateLimiter()) }
     * }
     * ```
     */
    fun requestHandler(handlerBuilder: (resources: ClientResources) -> RequestHandler) {
        this.handlerBuilder = handlerBuilder
    }


    /**
     * @throws KordInitializationException if something went wrong while getting the bot's gateway information.
     */
    fun build(): Kord {
        val client = httpClient.configure(token)

        val resources = ClientResources(token, 0, client, EntitySupplyStrategy.rest, Intents.none)
        val rest = RestClient(handlerBuilder(resources))
        val selfId = getBotIdFromToken(token)

        val eventPublisher = BroadcastChannel<Event>(1)

        return Kord(
                resources,
                DataCache.none(),
                MasterGateway(mapOf(0 to Gateway.none())),
                rest,
                selfId,
                eventPublisher,
                defaultDispatcher
        )
    }
}