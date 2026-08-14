package dev.kord.core.builder.kord

import dev.kord.common.annotation.KordInternal
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.gateway.Gateway
import dev.kord.rest.ratelimit.ExclusionRequestRateLimiter
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.StackTraceRecoveringKtorRequestHandler
import dev.kord.rest.request.withStackTraceRecovery
import dev.kord.rest.service.RestClient
import io.ktor.client.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Abstract base for all Kord builders.
 */
public sealed class AbstractKordBuilder {
    protected var handlerBuilder: (resources: ClientResources) -> RequestHandler =
        { KtorRequestHandler(it.httpClient, ExclusionRequestRateLimiter(), token = it.token) }

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
     * Enables stack trace recovery on the currently defined [RequestHandler].
     *
     * @throws IllegalStateException if the [RequestHandler] is not a [KtorRequestHandler]
     *
     * @see StackTraceRecoveringKtorRequestHandler
     * @see withStackTraceRecovery
     */
    public var stackTraceRecovery: Boolean = false

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

    protected fun buildRequestHandler(resources: ClientResources): RequestHandler {
        val rawRequestHandler = handlerBuilder(resources)
        return if (stackTraceRecovery) {
            if (rawRequestHandler is KtorRequestHandler) {
                rawRequestHandler.withStackTraceRecovery()
            } else {
                error("stackTraceRecovery only works with KtorRequestHandlers, please set stackTraceRecovery = false or use a different RequestHandler")
            }
        } else {
            rawRequestHandler
        }
    }
}

/**
 * Interface supposed to be used together with [AbstractKordBuilder] to add a application related information.
 *
 * @property token the token used for authentication
 * @property applicationId the id of the application
 */
public interface HasApplication {
    public val token: String
    public var applicationId: Snowflake?

    @KordInternal
    public val actualApplicationId: Snowflake
        get() = applicationId ?: getBotIdFromToken(token)
}
