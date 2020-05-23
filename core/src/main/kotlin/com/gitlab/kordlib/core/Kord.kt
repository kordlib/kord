package com.gitlab.kordlib.core

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.common.entity.DiscordPartialGuild
import com.gitlab.kordlib.common.entity.DiscordShard
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.rest.builder.guild.GuildCreateBuilder
import com.gitlab.kordlib.core.builder.kord.KordBuilder
import com.gitlab.kordlib.core.builder.presence.PresenceUpdateBuilder
import com.gitlab.kordlib.core.cache.KordCache
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.gateway.handler.GatewayEventInterceptor
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.start
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
        val rest: RestClient,
        val selfId: Snowflake,
        private val eventPublisher: BroadcastChannel<Event>,
        private val dispatcher: CoroutineDispatcher
) : CoroutineScope, EntitySupplier {
    private val interceptor = GatewayEventInterceptor(this, gateway, cache, eventPublisher)

    init {
        launch { interceptor.start() }
    }

    val cache: KordCache = KordCache(this, cache)

    @Suppress("EXPERIMENTAL_API_USAGE")
    val unsafe: Unsafe = Unsafe(this)

    val events get() = eventPublisher.asFlow()

    override val coroutineContext: CoroutineContext
        get() = dispatcher + Job()


    override val regions: Flow<Region>
        get() = flow {
            val request = flow {
                rest.voice.getVoiceRegions().forEach { emit(it) }
            }.map { RegionData.from(it) }.map { Region(it, this@Kord) }

            val flow = cache.regions.switchIfEmpty(request)

            emitAll(flow)
        }

    /**
     * Gets all guilds that are currently cached, if none are cached a request will be send to get all guilds.
     */
    override val guilds: Flow<Guild>
        get() = flow {
            //backup if we're not caching
            val request = paginateForwards(idSelector = DiscordPartialGuild::id, batchSize = 100) { position -> rest.user.getCurrentUserGuilds(position, 100) }
                    .map { rest.guild.getGuild(it.id, true) }
                    .map { GuildData.from(it) }
                    .map { Guild(it, this@Kord) }

            val flow = cache.guilds.switchIfEmpty(request)

            emitAll(flow)
        }

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

    suspend fun getApplicationInfo(): ApplicationInfo {
        val response = rest.application.getCurrentApplicationInfo()
        return ApplicationInfo(ApplicationInfoData.from(response), this)
    }

    suspend inline fun createGuild(builder: GuildCreateBuilder.() -> Unit): Guild {
        val response = rest.guild.createGuild(builder)
        val data = GuildData.from(response)

        return Guild(data, this)
    }

    override suspend fun getChannel(id: Snowflake): Channel? = cache.getChannel(id) ?: requestsChannel(id)

    override suspend fun getGuild(id: Snowflake): Guild? = cache.getGuild(id) ?: requestGuild(id)

    override suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member? {
        return cache.getMember(guildId = guildId, userId = userId) ?: requestMember(guildId = guildId, userId = userId)
    }

    override suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message? {
        return cache.getMessage(channelId = channelId, messageId = messageId)
                ?: requestMessage(channelId = channelId, messageId = messageId)
    }

    @Deprecated(message = "use regions instead", level = DeprecationLevel.WARNING, replaceWith = ReplaceWith("regions"))
    suspend fun getRegions(): Flow<Region> =
            rest.voice.getVoiceRegions().asFlow().map { RegionData.from(it) }.map { Region(it, this) }

    override suspend fun getRole(guildId: Snowflake, roleId: Snowflake): Role? {
        return cache.getRole(guildId = guildId, roleId = roleId) ?: requestRole(guildId = guildId, roleId = roleId)
    }

    override suspend fun getSelf(): User = cache.getSelf() ?: User(UserData.from(rest.user.getCurrentUser()), this)

    override suspend fun getUser(id: Snowflake): User? = cache.getUser(id) ?: requestUser(id)

    @Deprecated("use cache.users instead", ReplaceWith("cache.users"), DeprecationLevel.WARNING)
    suspend fun getUsers(): Flow<User> =
            cache.query<UserData>().asFlow().map { User(it, this) }

    suspend inline fun editPresence(builder: PresenceUpdateBuilder.() -> Unit) {
        val request = PresenceUpdateBuilder().apply(builder).toRequest()
        gateway.send(request)
    }

    override fun equals(other: Any?): Boolean {
        val kord = other as? Kord ?: return false

        return resources.token == kord.resources.token
    }

    internal suspend fun requestsChannel(id: Snowflake): Channel? {
        val data = catchNotFound { rest.channel.getChannel(id.value).let { ChannelData.from(it) } } ?: return null
        return Channel.from(data, this)
    }

    internal suspend fun requestGuild(id: Snowflake): Guild? {
        val data = catchNotFound { rest.guild.getGuild(id.value, true).let { GuildData.from(it) } } ?: return null
        return Guild(data, this)
    }

    internal suspend fun requestMember(guildId: Snowflake, userId: Snowflake): Member? {
        val response = catchNotFound {
            rest.guild.getGuildMember(guildId = guildId.value, userId = userId.value)
        } ?: return null

        val memberData = MemberData.from(guildId = guildId.value, userId = userId.value, entity = response)
        val userData = response.user?.let { UserData.from(it) } ?: catchNotFound {
            rest.user.getUser(userId.value).let { UserData.from(it) }
        } ?: return null //this shouldn't happen, since we already know the member to exist.

        return Member(memberData, userData, this)
    }

    internal suspend fun requestMessage(channelId: Snowflake, messageId: Snowflake): Message? {
        val data = catchNotFound {
            val response = rest.channel.getMessage(channelId.value, messageId.value)
            MessageData.from(response)
        } ?: return null

        return Message(data, this)
    }

    internal suspend fun requestRole(guildId: Snowflake, roleId: Snowflake): Role? {
        val data = catchNotFound {
            val response = rest.guild.getGuildRoles(guildId.value)
                    .firstOrNull { it.id == roleId.value } ?: return@catchNotFound null

            RoleData.from(guildId.value, response)
        } ?: return null

        return Role(data, this)
    }

    internal suspend fun requestUser(id: Snowflake): User? {
        val data = catchNotFound { rest.user.getUser(id.value).let { UserData.from(it) } } ?: return null
        return User(data, this)
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
