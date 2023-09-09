package dev.kord.core.builder.kord

import dev.kord.cache.api.DataCache
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.gateway.handler.GatewayEventInterceptor
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Gateway
import dev.kord.gateway.builder.Shards
import dev.kord.rest.ratelimit.ExclusionRequestRateLimiter
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.service.RestClient
import io.ktor.client.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * The Builder for the RestOnly client
 */
public abstract class RestOnlyBuilder {
    protected var handlerBuilder: (resources: ClientResources) -> RequestHandler =
        { KtorRequestHandler(it.httpClient, ExclusionRequestRateLimiter(), token = it.token) }

    protected abstract val token: String

    /**
     * The [CoroutineDispatcher] kord uses to launch suspending tasks. [Dispatchers.Default] by default.
     */
    public var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    /**
     * The client used for building [Gateways][Gateway] and [RequestHandlers][RequestHandler]. A default implementation
     * will be used when not set.
     */
    public var httpClient: HttpClient? = null

    /**
     * The [Snowflake] ID for the application
     */
    public abstract var applicationId: Snowflake

    /**
     * Configures the [RequestHandler] for the [RestClient].
     *
     * ```
     * Kord(token) {
     *   requestHandler  { resources -> KtorRequestHandler(resources.httpClient, ExclusionRequestRateLimiter()) }
     * }
     * ```
     */
    public fun requestHandler(handlerBuilder: (resources: ClientResources) -> RequestHandler) {
        this.handlerBuilder = handlerBuilder
    }

    /**
     * Builds the rest only [Kord] instance
     */
    public fun build(): Kord {
        val client = httpClient.configure()
        val selfId = applicationId

        val resources = ClientResources(
            token,
            selfId,
            Shards(0),
            maxConcurrency = 1,
            client,
            EntitySupplyStrategy.rest,
        )

        val rest = RestClient(handlerBuilder(resources))

        return Kord(
            resources = resources,
            cache = @OptIn(ExperimentalCoroutinesApi::class) DataCache.none(),
            gateway = DefaultMasterGateway(mapOf(0 to Gateway.none())),
            rest = rest,
            selfId = selfId,
            eventFlow = MutableSharedFlow(),
            dispatcher = defaultDispatcher,
            interceptor = GatewayEventInterceptor.none(),
        )
    }
}
