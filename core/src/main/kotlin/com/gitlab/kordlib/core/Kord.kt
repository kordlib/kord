package com.gitlab.kordlib.core

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.common.annotation.KordExperimental
import com.gitlab.kordlib.common.annotation.KordUnsafe
import com.gitlab.kordlib.common.entity.DiscordShard
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.builder.kord.KordBuilder
import com.gitlab.kordlib.core.builder.kord.KordRestOnlyBuilder
import com.gitlab.kordlib.core.cache.data.GuildData
import com.gitlab.kordlib.core.entity.ApplicationInfo
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Region
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.exception.KordInitializationException
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.core.gateway.handler.GatewayEventInterceptor
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.builder.PresenceBuilder
import com.gitlab.kordlib.rest.builder.guild.GuildCreateBuilder
import com.gitlab.kordlib.rest.service.RestClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.channels.Channel as CoroutineChannel

val kordLogger = KotlinLogging.logger { }

class Kord(
        val resources: ClientResources,
        val cache: DataCache,
        val gateway: MasterGateway,
        val rest: RestClient,
        val selfId: Snowflake,
        private val eventPublisher: BroadcastChannel<Event>,
        private val dispatcher: CoroutineDispatcher,
) : CoroutineScope {
    private val interceptor = GatewayEventInterceptor(this, gateway, cache, eventPublisher)

    init {
        launch { interceptor.start() }
    }

    val defaultSupplier: EntitySupplier = resources.defaultStrategy.supply(this)

    @OptIn(KordUnsafe::class)
    val unsafe: Unsafe = Unsafe(this)

    val events get() = eventPublisher.asFlow().buffer(CoroutineChannel.UNLIMITED)

    override val coroutineContext: CoroutineContext
        get() = dispatcher + Job()

    val regions: Flow<Region>
        get() = resources.defaultStrategy.supply(this).regions

    val guilds: Flow<Guild>
        get() = resources.defaultStrategy.supply(this).guilds

    /**
     * Logs in to the configured [Gateways][Gateway]. Suspends until [logout] or [shutdown] is called.
     */
    @OptIn(ExperimentalContracts::class)
    suspend inline fun login(builder: PresenceBuilder.() -> Unit = { status = Status.Online }) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        gateway.start(resources.token) {
            shard = DiscordShard(0, resources.shardCount)
            presence(builder)
            intents = resources.intents
            name = "kord"
        }
    }

    /**
     * Logs out to the configured [Gateways][Gateway].
     */
    suspend fun logout() = gateway.stopAll()

    /**
     * Logs out of all connected [Gateways][Gateway] and frees all resources.
     */
    suspend fun shutdown() {
        gateway.detachAll()
        this.eventPublisher.close()
    }

    fun <T : EntitySupplier> with(strategy: EntitySupplyStrategy<T>): T = strategy.supply(this)

    suspend fun getApplicationInfo(): ApplicationInfo = with(EntitySupplyStrategy.rest).getApplicationInfo()

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuild(builder: GuildCreateBuilder.() -> Unit): Guild {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val response = rest.guild.createGuild(builder)
        val data = GuildData.from(response)

        return Guild(data, this)
    }

    suspend fun getChannel(id: Snowflake, strategy: EntitySupplyStrategy<*> = resources.defaultStrategy): Channel? = strategy.supply(this).getChannelOrNull(id)

    /**
     * Requests to get the [Channel] as type [T] through the [strategy],
     * returns null if the [Channel] isn't present or is not of type [T].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend inline fun <reified T : Channel> getChannelOf(
            id: Snowflake,
            strategy: EntitySupplyStrategy<*> = resources.defaultStrategy,
    ): T? = strategy.supply(this).getChannelOfOrNull(id)

    suspend fun getGuild(id: Snowflake, strategy: EntitySupplyStrategy<*> = resources.defaultStrategy): Guild? = strategy.supply(this).getGuildOrNull(id)

    suspend fun getSelf(strategy: EntitySupplyStrategy<*> = resources.defaultStrategy): User =
            strategy.supply(this).getSelf()

    suspend fun getUser(id: Snowflake, strategy: EntitySupplyStrategy<*> = resources.defaultStrategy): User? =
            strategy.supply(this).getUserOrNull(id)

    @OptIn(ExperimentalContracts::class)
    suspend inline fun editPresence(builder: PresenceBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val status = PresenceBuilder().apply(builder).toUpdateStatus()
        gateway.sendAll(status)
    }

    override fun equals(other: Any?): Boolean {
        val kord = other as? Kord ?: return false

        return resources.token == kord.resources.token
    }

    companion object {

        /**
         * Builds a [Kord] instance configured by the [builder].
         *
         * @throws KordInitializationException if something went wrong while getting the bot's gateway information.
         */
        suspend inline operator fun invoke(token: String, builder: KordBuilder.() -> Unit = {}): Kord =
                KordBuilder(token).apply(builder).build()

        /**
         * Builds a [Kord] instance configured by the [builder].
         *
         * The instance only allows for configuration of REST related APIs,
         * interacting with the [gateway][Kord.gateway] or its [events][Kord.events] will result in no-ops.
         *
         * Similarly, [cache][Kord.cache] related functionality has been disabled and
         * replaced with a no-op implementation.
         */
        @OptIn(ExperimentalContracts::class)
        @KordExperimental
        inline fun restOnly(token: String, builder: KordRestOnlyBuilder.() -> Unit = {}): Kord {
            contract {
                callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
            }
            return KordRestOnlyBuilder(token).apply(builder).build()
        }
    }

}

/**
 * Convenience method that will invoke the [consumer] on every event [T], the consumer is launched in the given [scope]
 * or [Kord] by default and will not propagate any exceptions.
 */
inline fun <reified T : Event> Kord.on(scope: CoroutineScope = this, noinline consumer: suspend T.() -> Unit) =
        events.buffer(CoroutineChannel.UNLIMITED).filterIsInstance<T>().onEach {
            runCatching { consumer(it) }.onFailure { kordLogger.catching(it) }
        }.catch { kordLogger.catching(it) }.launchIn(scope)
