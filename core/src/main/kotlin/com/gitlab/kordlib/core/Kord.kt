package com.gitlab.kordlib.core

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.common.entity.DiscordPartialGuild
import com.gitlab.kordlib.common.entity.DiscordShard
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.rest.builder.guild.GuildCreateBuilder
import com.gitlab.kordlib.core.builder.kord.KordBuilder
import com.gitlab.kordlib.core.builder.presence.PresenceUpdateBuilder
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
        val cache: DataCache,
        val gateway: Gateway,
        val rest: RestClient,
        val selfId: Snowflake,
        private val eventPublisher: BroadcastChannel<Event>,
        private val dispatcher: CoroutineDispatcher
) : CoroutineScope {
    private val interceptor = GatewayEventInterceptor(this, gateway, cache, eventPublisher)
    init {
        launch { interceptor.start() }
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    val unsafe: Unsafe = Unsafe(this)

    val events get() = eventPublisher.asFlow()

    override val coroutineContext: CoroutineContext
        get() = dispatcher + Job()

    /**
     * Gets all guilds that are currently cached, if none are cached a request will be send to get all guilds.
     */
    val guilds: Flow<Guild>
        get() = flow {
            val cached = cache.find<GuildData>().asFlow().map { Guild(it, this@Kord) }

            //backup if we're not caching
            val request = paginateForwards(idSelector = DiscordPartialGuild::id, batchSize = 100) { position -> rest.user.getCurrentUserGuilds(position, 100) }
                    .map { rest.guild.getGuild(it.id) }
                    .map { GuildData.from(it) }
                    .map { Guild(it, this@Kord) }

            var none = true

            cached.collect {
                none = false
                emit(it)
            }

            if (none) emitAll(request)
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

    suspend fun getChannel(id: Snowflake): Channel? {
        val data = getChannelData(id) ?: return null

        return Channel.from(data, this)
    }

    suspend fun getGuild(guildId: Snowflake): Guild? {
        val data = getGuildData(guildId) ?: return null

        return Guild(data, this)
    }

    suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member? {
        val memberData = getMemberData(guildId, userId) ?: return null
        val userData = getUserData(userId) ?: return null

        return Member(memberData, userData, this)
    }

    suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message? {
        val data = getMessageData(channelId, messageId) ?: return null

        return Message(data, this)
    }

    suspend fun getRegions(): Flow<Region> =
            rest.voice.getVoiceRegions().asFlow().map { RegionData.from(it) }.map { Region(it, this) }

    suspend fun getRole(guildId: Snowflake, id: Snowflake): Role? {
        val data = getRoleData(guildId, id) ?: return null

        return Role(data, this)
    }

    suspend fun getSelf(): User {
        val cached = cache.find<UserData> { UserData::id eq selfId.longValue }.singleOrNull()

        return User(cached ?: UserData.from(rest.user.getCurrentUser()), this)
    }

    suspend fun getUser(userId: Snowflake): User? {
        val data = getUserData(userId) ?: return null

        return User(data, this)
    }

    suspend fun getUsers(): Flow<User> =
            cache.find<UserData>().asFlow().map { User(it, this) }

    suspend inline fun editPresence(builder: PresenceUpdateBuilder.() -> Unit) {
        val request = PresenceUpdateBuilder().apply(builder).toRequest()
        gateway.send(request)
    }

    override fun equals(other: Any?): Boolean {
        val kord = other as? Kord ?: return false

        return resources.token == kord.resources.token
    }

    internal suspend fun getChannelData(id: Snowflake): ChannelData? {
        val cached = cache.find<ChannelData> { ChannelData::id eq id.longValue }.singleOrNull()

        return cached ?: catchNotFound { rest.channel.getChannel(id.value).let { ChannelData.from(it) } }
    }

    internal suspend fun getGuildData(id: Snowflake): GuildData? {
        val cached = cache.find<GuildData> { GuildData::id eq id.longValue }.singleOrNull()

        return cached ?: catchNotFound { rest.guild.getGuild(id.value).let { GuildData.from(it) } }
    }

    internal suspend fun getMemberData(guildId: Snowflake, id: Snowflake): MemberData? {
        val cached = cache.find<MemberData> { MemberData::userId eq id.longValue }.singleOrNull()

        return cached ?: catchNotFound {
            val response = rest.guild.getGuildMember(guildId = guildId.value, userId = id.value)
            MemberData.from(userId = id.value, guildId = guildId.value, entity = response)
        }
    }

    internal suspend fun getMessageData(channelId: Snowflake, id: Snowflake): MessageData? {
        val cached = cache.find<MessageData> {
            MessageData::id eq id.longValue
            MessageData::channelId eq channelId.longValue
        }.singleOrNull()

        return cached ?: catchNotFound {
            val response = rest.channel.getMessage(channelId.value, id.value)
            MessageData.from(response)
        }
    }

    internal suspend fun getRoleData(guildId: Snowflake, id: Snowflake): RoleData? {
        val cached = cache.find<RoleData> {
            RoleData::id eq id.longValue
            RoleData::guildId eq guildId.longValue
        }.singleOrNull()

        return cached ?: catchNotFound {
            val response = rest.guild.getGuildRoles(guildId.value)
                    .firstOrNull { it.id == id.value } ?: return@catchNotFound null

            RoleData.from(guildId.value, response)
        }
    }

    internal suspend fun getUserData(id: Snowflake): UserData? {
        val cached = cache.find<UserData> { UserData::id eq id.longValue }.singleOrNull()

        return cached ?: catchNotFound { rest.user.getUser(id.value).let { UserData.from(it) } }
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

