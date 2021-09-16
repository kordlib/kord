package dev.kord.core.builder.kord

import dev.kord.cache.api.DataCache
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.exception.KordInitializationException
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Gateway
import dev.kord.gateway.Intents
import dev.kord.rest.ratelimit.ExclusionRequestRateLimiter
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.service.RestClient
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow

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

    var applicationId: Snowflake? = null

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

        val resources = ClientResources(
            token,
            applicationId ?: getBotIdFromToken(token),
            Shards(0),
            client,
            EntitySupplyStrategy.rest,
            Intents.none
        )
        val rest = RestClient(handlerBuilder(resources))
        val selfId = getBotIdFromToken(token)

        return Kord(
            resources,
            DataCache.none(),
            DefaultMasterGateway(mapOf(0 to Gateway.none())),
            rest,
            selfId,
            MutableSharedFlow(),
            defaultDispatcher,
            null
        )
    }
}