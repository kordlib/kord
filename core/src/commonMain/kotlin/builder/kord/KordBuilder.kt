package dev.kord.core.builder.kord

import dev.kord.cache.api.DataCache
import dev.kord.common.KordConstants
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.cache.CachingGateway
import dev.kord.core.cache.KordCacheBuilder
import dev.kord.core.cache.createView
import dev.kord.core.cache.registerKordData
import dev.kord.core.event.Event
import dev.kord.core.exception.KordInitializationException
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.kord.core.gateway.handler.GatewayEventInterceptor
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.DefaultGateway
import dev.kord.gateway.Gateway
import dev.kord.gateway.builder.Shards
import dev.kord.gateway.ratelimit.IdentifyRateLimiter
import dev.kord.rest.json.response.BotGatewayResponse
import dev.kord.rest.request.*
import dev.kord.rest.route.Route
import dev.kord.rest.service.RestClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpHeaders.UserAgent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.json.Json

private val gatewayInfoJson = Json { ignoreUnknownKeys = true }

public expect class KordBuilder(token: String) : BaseKordBuilder

public abstract class BaseKordBuilder internal constructor(override val token: String) : AbstractKordBuilder(), HasApplication {
    private var shardsBuilder: (recommended: Int) -> Shards = { Shards(it) }
    private var gatewayBuilder: (resources: ClientResources, shards: List<Int>) -> List<Gateway> =
        { resources, shards ->
            // shared between all shards
            val rateLimiter = IdentifyRateLimiter(resources.maxConcurrency, defaultDispatcher)
            shards.map {
                DefaultGateway {
                    client = resources.httpClient
                    identifyRateLimiter = rateLimiter
                }
            }
        }

    override var applicationId: Snowflake? = null
    private var cacheBuilder: KordCacheBuilder.(resources: ClientResources) -> Unit = {}

    /**
     * The event flow used by [Kord.eventFlow] to publish [events][Kord.events].
     *
     *
     * By default, a [MutableSharedFlow] with an `extraBufferCapacity` of `Int.MAX_VALUE` is used.
     */
    public var eventFlow: MutableSharedFlow<Event> = MutableSharedFlow(
        extraBufferCapacity = Int.MAX_VALUE
    )

    /**
     * The default strategy used by entities to retrieve entities. [EntitySupplyStrategy.cacheWithRestFallback] by default.
     */
    public var defaultStrategy: EntitySupplyStrategy<*> = EntitySupplyStrategy.cacheWithRestFallback


    /**
     * The [GatewayEventInterceptor] used for converting [gateway events][dev.kord.gateway.Event] to
     * [core events][dev.kord.core.event.Event].
     *
     * [DefaultGatewayEventInterceptor] will be used when not set.
     */
    public var gatewayEventInterceptor: GatewayEventInterceptor? = null

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
    public fun sharding(shards: (recommended: Int) -> Shards) {
        this.shardsBuilder = shards
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
    public fun gateways(gatewayBuilder: (resources: ClientResources, shards: List<Int>) -> List<Gateway>) {
        this.gatewayBuilder = gatewayBuilder
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
    public fun cache(builder: KordCacheBuilder.(resources: ClientResources) -> Unit) {
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
        val response = get("${Route.baseUrl}${Route.GatewayBotGet.path}") {
            header(UserAgent, KordConstants.USER_AGENT)
            header(Authorization, "Bot $token")
        }
        val responseBody = response.bodyAsText()
        if (response.isError) {
            val message = buildString {
                append("Something went wrong while initializing Kord")
                if (response.status == HttpStatusCode.Unauthorized) {
                    append(", make sure the bot token you entered is valid.")
                } else {
                    append(". ")
                }

                appendLine("Discord response: $responseBody (${response.status})")
            }

            throw KordInitializationException(message)
        }

        return gatewayInfoJson.decodeFromString(BotGatewayResponse.serializer(), responseBody)
    }

    /**
     * @throws KordInitializationException if something went wrong while getting the bot's gateway information.
     */
    public open suspend fun build(): Kord = buildBase()

    protected suspend fun buildBase(): Kord {
        val client = httpClient.configure()

        val gatewayInfo = client.getGatewayInfo()
        val recommendedShards = gatewayInfo.shards
        val shardsInfo = shardsBuilder(recommendedShards)
        val shards = shardsInfo.indices.toList()

        val resources = ClientResources(
            token = token,
            applicationId = actualApplicationId,
            shards = shardsInfo,
            maxConcurrency = gatewayInfo.sessionStartLimit.maxConcurrency,
            httpClient = client,
            defaultStrategy = defaultStrategy,
        )
        val requestHandler = buildRequestHandler(resources)

        val rest = RestClient(requestHandler)
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
            DefaultMasterGateway(gateways)
        }

        val self = getBotIdFromToken(token)

        return Kord(
            resources = resources,
            cache = cache,
            gateway = gateway,
            rest = rest,
            selfId = self,
            eventFlow = eventFlow,
            dispatcher = defaultDispatcher,
            interceptor = gatewayEventInterceptor ?: DefaultGatewayEventInterceptor(),
        )
    }
}
