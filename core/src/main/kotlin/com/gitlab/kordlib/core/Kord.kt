package com.gitlab.kordlib.core

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.common.entity.DiscordPartialGuild
import com.gitlab.kordlib.common.entity.DiscordShard
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.core.builder.kord.KordBuilder
import com.gitlab.kordlib.core.builder.presence.PresenceUpdateBuilder
import com.gitlab.kordlib.core.cache.KordCache
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.gateway.handler.GatewayEventInterceptor
import com.gitlab.kordlib.core.rest.KordRestClient
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.start
import com.gitlab.kordlib.rest.builder.guild.GuildCreateBuilder
import com.gitlab.kordlib.rest.service.RestClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.channels.Channel as CoroutineChannel

val kordLogger = KotlinLogging.logger { }

class Kord internal constructor(
        val resources: ClientResources,
        cache: DataCache,
        val gateway: Gateway,
        rest: RestClient,
        val selfId: Snowflake,
        private val eventPublisher: BroadcastChannel<Event>,
        private val dispatcher: CoroutineDispatcher
) : CoroutineScope {
    private val interceptor = GatewayEventInterceptor(this, gateway, cache, eventPublisher)

    init {
        launch { interceptor.start() }
    }

    val cache: KordCache = KordCache(this, cache)

    val rest: KordRestClient = KordRestClient(this, rest)

    @Suppress("EXPERIMENTAL_API_USAGE")
    val unsafe: Unsafe = Unsafe(this)

    val events get() = eventPublisher.asFlow()

    override val coroutineContext: CoroutineContext
        get() = dispatcher + Job()


    val regions: Flow<Region>
        get() = resources.defaultStrategy.supply(this).regions

    /**
     * Gets all guilds from the [ClientResources.defaultStrategy].
     */
    val guilds: Flow<Guild>
        get() = resources.defaultStrategy.supply(this).guilds

    /**
     * Logs in to the configured [Gateways][Gateway]. Suspends until [logout] or [shutdown] is called.
     */
    suspend inline fun login(builder: PresenceUpdateBuilder.() -> Unit = { status = Status.Online }) = gateway.start(resources.token) {
        shard = DiscordShard(0, resources.shardCount)
        presence = PresenceUpdateBuilder().apply(builder).toGatewayPresence()
        name = "kord"
    }

    /**
     * Logs out to the configured [Gateways][Gateway].
     */
    suspend fun logout() = gateway.stop()

    /**
     * Logs out of all connected [Gateways][Gateway] and frees all resources.
     */
    suspend fun shutdown() {
        gateway.detach()
        this.eventPublisher.close()
    }

    fun with(strategy: EntitySupplyStrategy) = strategy.supply(this)

    suspend fun getApplicationInfo(): ApplicationInfo {
        val response = rest.application.getCurrentApplicationInfo()
        return ApplicationInfo(ApplicationInfoData.from(response), this)
    }

    suspend inline fun createGuild(builder: GuildCreateBuilder.() -> Unit): Guild {
        val response = rest.guild.createGuild(builder)
        val data = GuildData.from(response)

        return Guild(data, this)
    }

    suspend fun getChannel(id: Snowflake, strategy: EntitySupplyStrategy = resources.defaultStrategy): Channel? = strategy.supply(this).getChannel(id)

    suspend fun getGuild(id: Snowflake, strategy: EntitySupplyStrategy = resources.defaultStrategy): Guild? = strategy.supply(this).getGuild(id)

    suspend fun getMember(guildId: Snowflake, userId: Snowflake, strategy: EntitySupplyStrategy = resources.defaultStrategy): Member? =
            strategy.supply(this).getMember(guildId, userId)

    suspend fun getMessage(channelId: Snowflake, messageId: Snowflake, strategy: EntitySupplyStrategy = resources.defaultStrategy): Message? =
            strategy.supply(this).getMessage(channelId, messageId)

    suspend fun getRole(guildId: Snowflake, roleId: Snowflake, strategy: EntitySupplyStrategy = resources.defaultStrategy): Role? =
        strategy.supply(this).getRole(guildId, roleId)

    suspend fun getSelf(strategy: EntitySupplyStrategy = resources.defaultStrategy): User =
            strategy.supply(this).getSelf() ?: rest.getSelf()

        suspend fun getUser(id: Snowflake, strategy: EntitySupplyStrategy = resources.defaultStrategy): User? =
                strategy.supply(this).getUser(id)

    suspend inline fun editPresence(builder: PresenceUpdateBuilder.() -> Unit) {
        val request = PresenceUpdateBuilder().apply(builder).toRequest()
        gateway.send(request)
    }

    override fun equals(other: Any?): Boolean {
        val kord = other as? Kord ?: return false

        return resources.token == kord.resources.token
    }

    companion object {
        suspend inline operator fun invoke(token: String, builder: KordBuilder.() -> Unit = {}) =
                KordBuilder(token).apply(builder).build()
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
