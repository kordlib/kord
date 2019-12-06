@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.gitlab.kordlib.core.builder.kord

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.common.ratelimit.BucketRateLimiter
import com.gitlab.kordlib.core.ClientResources
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.CachingGateway
import com.gitlab.kordlib.core.cache.KordCacheBuilder
import com.gitlab.kordlib.core.cache.createView
import com.gitlab.kordlib.core.cache.registerKordData
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.core.gateway.handler.GatewayEventInterceptor
import com.gitlab.kordlib.gateway.DefaultGateway
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.retry.LinearRetry
import com.gitlab.kordlib.gateway.retry.Retry
import com.gitlab.kordlib.rest.json.response.BotGatewayResponse
import com.gitlab.kordlib.rest.ratelimit.ExclusionRequestHandler
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Route
import com.gitlab.kordlib.rest.service.RestClient
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import kotlin.concurrent.thread
import kotlin.time.seconds

private val logger = KotlinLogging.logger { }

operator fun DefaultGateway.Companion.invoke(resources: ClientResources, retry: Retry) =
        DefaultGateway("wss://gateway.discord.gg/", resources.httpClient, retry, BucketRateLimiter(120, 60.seconds))

class KordBuilder(val token: String) {
    private var shardRange: (recommended: Int) -> Iterable<Int> = { 0 until it }
    private var gatewayBuilder: (resources: ClientResources, shard: Int) -> Gateway = { resources, _ ->
        DefaultGateway(resources, LinearRetry(2.seconds, 60.seconds, 10))
    }

    private var handlerBuilder: (resources: ClientResources) -> RequestHandler = { ExclusionRequestHandler(it.httpClient) }
    private var cacheBuilder: KordCacheBuilder.(resources: ClientResources) -> Unit = {}

    /**
     * Enable adding a [Runtime.addShutdownHook] to log out of the [Gateway] when the process is killed.
     */
    var enableShutdownHook: Boolean = true

    /**
     * The [CoroutineDispatcher] kord uses to launch suspending tasks. [Dispatchers.IO] by default.
     */
    var defaultDispatcher: CoroutineDispatcher = Dispatchers.IO

    /**
     * The client used for building [Gateways][Gateway] and [RequestHandlers][RequestHandler]. A default implementation
     * will be used when not set.
     */
    var httpClient: HttpClient? = null

    /**
     * Configures the shards this client will connect to, by default `0 until recommended`.
     * This can be used to break up to client into multiple processes.
     *
     * ```
     * cache {
     *  defaultGenerator = lruCache(10)
     *  forDescription(UserData.description) { cache, description ->  DataEntryCache.none() }
     *  forDescription(MessageData.description) { cache, description ->
     *      MapEntryCache(cache, description, MapLikeCollection.lruLinkedHashMap(100))
     *  }
     *  forDescription(UserData.description) { cache, description ->
     *      MapEntryCache(cache, description, MapLikeCollection.weakHashMap())
     *  }
     *}
     * ```
     */
    fun sharding(shardRange: (recommended: Int) -> Iterable<Int>) {
        this.shardRange = shardRange
    }

    /**
     * Configures the [Gateway] for each shard.
     *
     * ```
     * Kord(token) {
     *     { resources, shard -> DefaultGateway(resources, LinearRetry(2.seconds, 60.seconds, 10)) }
     * }
     * ```
     */
    fun gateway(gatewayBuilder: (resources: ClientResources, shard: Int) -> Gateway) {
        this.gatewayBuilder = gatewayBuilder
    }

    /**
     * Configures the [RequestHandler] for the [RestClient].
     *
     * ```
     * Kord(token) {
     *     { resources -> ExclusionRequestHandler(resources.httpClient) }
     * }
     * ```
     */
    fun requestHandler(handlerBuilder: (resources: ClientResources) -> RequestHandler) {
        this.handlerBuilder = handlerBuilder
    }

    /**
     * Configures the [DataCache] for caching.
     *
     *  ```
     * Kord(token) {
     *     cache {
     *         defaultGenerator = lruCache()
     *         forDescription(MessageData.description) { cache, description -> DataEntryCache.none() }
     *         forDescription(UserData.description) { cache, description -> MapEntryCache(cache, description, MapLikeCollection.weakHashMap()) }
     *     }
     * }
     * ```
     */
    fun cache(builder: KordCacheBuilder.(resources: ClientResources) -> Unit) {
        val old = cacheBuilder
        cacheBuilder = { resources: ClientResources ->
            old(resources)
            builder(resources)
        }
    }

    private fun HttpClientConfig<*>.defaultConfig() {
        defaultRequest {
            header("Authorization", "Bot $token")
        }

        install(JsonFeature)
        install(WebSockets)
    }

    suspend fun build(): Kord {
        val client = httpClient?.let {
            it.config { defaultConfig() }
        } ?: run {
            HttpClient(CIO) { defaultConfig() }
        }

        val response = client.get<BotGatewayResponse>("${Route.baseUrl}/gateway/bot")
        val recommendedShards = response.shards
        val shards = shardRange(recommendedShards).toList()

        if (client.engine.config.threadsCount < shards.size + 1) {
            logger.warn { """
                kord's http client is currently using ${client.engine.config.threadsCount} threads, 
                which is less than the advised threadcount of ${shards.size + 1} (number of shards + 1)""".trimIndent()
            }
        }

        val resources = ClientResources(token, shards.count(), client)
        val rest = RestClient(handlerBuilder(resources))
        val cache = KordCacheBuilder().apply { cacheBuilder(resources) }.build()
        cache.registerKordData()
        val gateway = run {
            val gateways = shards
                    .map { gatewayBuilder(resources, it) }
                    .map { CachingGateway(cache.createView(), it) }
                    .onEach { it.registerKordData() }

            MasterGateway(gateways, shards)
        }

        val self = rest.user.getCurrentUser().id

        val eventPublisher = BroadcastChannel<Event>(Channel.CONFLATED)

        if (enableShutdownHook) {
            Runtime.getRuntime().addShutdownHook(thread(false) {
                runBlocking {
                    gateway.detach()
                }
            })
        }

        return Kord(
                resources,
                cache,
                gateway,
                rest,
                Snowflake(self),
                eventPublisher,
                defaultDispatcher
        ).also {
            it.launch { GatewayEventInterceptor(it, gateway, cache, eventPublisher).start() }
        }
    }

}