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
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.exception.KordInitializationException
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.gateway.DefaultGateway
import com.gitlab.kordlib.gateway.DefaultGatewayData
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.Intents
import com.gitlab.kordlib.gateway.retry.LinearRetry
import com.gitlab.kordlib.gateway.retry.Retry
import com.gitlab.kordlib.rest.json.response.BotGatewayResponse
import com.gitlab.kordlib.rest.ratelimit.ExclusionRequestRateLimiter
import com.gitlab.kordlib.rest.request.KtorRequestHandler
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.request.isError
import com.gitlab.kordlib.rest.route.Route
import com.gitlab.kordlib.rest.service.RestClient
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.concurrent.thread
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.seconds

operator fun DefaultGateway.Companion.invoke(resources: ClientResources, retry: Retry = LinearRetry(2.seconds, 60.seconds, 10)) =
        DefaultGateway(DefaultGatewayData("wss://gateway.discord.gg/", resources.httpClient, retry, BucketRateLimiter(120, 60.seconds), BucketRateLimiter(1, 5.seconds)))

private val logger = KotlinLogging.logger { }

class KordBuilder(val token: String) {
    private var shardRange: (recommended: Int) -> Iterable<Int> = { 0 until it }
    private var gatewayBuilder: (resources: ClientResources, shards: List<Int>) -> List<Gateway> = { resources, shards ->
        val rateLimiter = BucketRateLimiter(1, 5.seconds)
        shards.map {
            DefaultGateway {
                client = resources.httpClient
                identifyRateLimiter = rateLimiter
            }
        }
    }

    private var handlerBuilder: (resources: ClientResources) -> RequestHandler =
            { KtorRequestHandler(it.httpClient, ExclusionRequestRateLimiter()) }
    private var cacheBuilder: KordCacheBuilder.(resources: ClientResources) -> Unit = {}

    /**
     * Enable adding a [Runtime.addShutdownHook] to log out of the [Gateway] when the process is killed.
     */
    var enableShutdownHook: Boolean = true

    /**
     * The [CoroutineDispatcher] kord uses to launch suspending tasks. [Dispatchers.Default] by default.
     */
    var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    /**
     * The default strategy used by entities to retrieve entities. [EntitySupplyStrategy.cacheWithRestFallback] by default.
     */
    var defaultStrategy: EntitySupplyStrategy<*> = EntitySupplyStrategy.cacheWithRestFallback

    /**
     * The client used for building [Gateways][Gateway] and [RequestHandlers][RequestHandler]. A default implementation
     * will be used when not set.
     */
    var httpClient: HttpClient? = null

    /**
     * The enabled gateway intents, setting intents to null will disable the feature.
     */
    var intents: Intents? = null

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
     *     gateway { resources, shards ->
     *         shards.map { DefaultGateway(resources) }
     *     }
     * }
     * ```
     */
    fun gateways(gatewayBuilder: (resources: ClientResources, shards: List<Int>) -> List<Gateway>) {
        this.gatewayBuilder = gatewayBuilder
    }



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


    /**
     * Requests the gateway info for the bot, or throws a [KordInitializationException] when something went wrong.
     */
    private suspend fun HttpClient.getGatewayInfo(): BotGatewayResponse {
        val response = get<HttpResponse>("${Route.baseUrl}${Route.GatewayBotGet.path}")
        val responseBody = response.readText()
        if (response.isError) {
            val message = buildString {
                append("Something went wrong while initializing Kord.")
                if (response.status == HttpStatusCode.Unauthorized) {
                    append(", make sure the bot token you entered is valid.")
                }

                appendLine(responseBody)
            }

            throw KordInitializationException(message)
        }

        return Json { ignoreUnknownKeys = true }.decodeFromString(BotGatewayResponse.serializer(), responseBody)
    }

    /**
     * @throws KordInitializationException if something went wrong while getting the bot's gateway information.
     */
    suspend fun build(): Kord {
        val client = httpClient.configure(token)

        val recommendedShards = client.getGatewayInfo().shards
        val shards = shardRange(recommendedShards).toList()

        if (client.engine.config.threadsCount < shards.size + 1) {
            logger.warn {
                """
                kord's http client is currently using ${client.engine.config.threadsCount} threads, 
                which is less than the advised threadcount of ${shards.size + 1} (number of shards + 1)""".trimIndent()
            }
        }

        val resources = ClientResources(token, shards.count(), client, defaultStrategy, intents)
        val rest = RestClient(handlerBuilder(resources))
        val cache = KordCacheBuilder().apply { cacheBuilder(resources) }.build()
        cache.registerKordData()
        val gateway = run {
            val gateways = buildMap<Int, Gateway> {
                val gateways = gatewayBuilder(resources, shards)
                        .map { CachingGateway(cache.createView(), it) }
                        .onEach { it.registerKordData() }

                shards.forEachIndexed { index, shard ->
                    put(shard, gateways[index])
                }
            }
            MasterGateway(gateways)
        }

        val self = getBotIdFromToken(token)

        val eventPublisher = BroadcastChannel<Event>(1)

        if (enableShutdownHook) {
            Runtime.getRuntime().addShutdownHook(thread(false) {
                runBlocking {
                    gateway.detachAll()
                }
            })
        }

        return Kord(
                resources,
                cache,
                gateway,
                rest,
                self,
                eventPublisher,
                defaultDispatcher
        )
    }

}
