package com.gitlab.kordlib.core.cache

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.DataEntryCache
import com.gitlab.kordlib.cache.api.data.DataDescription
import com.gitlab.kordlib.cache.api.delegate.DelegatingDataCache
import com.gitlab.kordlib.cache.api.delegate.EntrySupplier
import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.cache.map.MapLikeCollection
import com.gitlab.kordlib.cache.map.internal.MapEntryCache
import com.gitlab.kordlib.cache.map.lruLinkedHashMap
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplier
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

typealias Generator<I, T> = (cache: DataCache, description: DataDescription<T, I>) -> DataEntryCache<out T>

@Deprecated("function will be removed", ReplaceWith("KordCacheBuilder().apply(builder).build()"), DeprecationLevel.WARNING)
@Suppress("FunctionName")
inline fun KordCache(builder: KordCacheBuilder.() -> Unit): DataCache = KordCacheBuilder().apply(builder).build()

class KordCache(val kord: Kord, val cache: DataCache) : DataCache by cache, EntitySupplier {

    val channels: Flow<Channel>
        get() = query<ChannelData>().asFlow().map { Channel.from(it, kord) }

    override val guilds: Flow<Guild>
        get() = query<GuildData>().asFlow().map { Guild(it, kord) }

    override val regions: Flow<Region>
        get() = query<RegionData>().asFlow().map { Region(it, kord) }

    val roles: Flow<Role>
        get() = query<RoleData>().asFlow().map { Role(it, kord) }

    val users: Flow<User>
        get() = query<UserData>().asFlow().map { User(it, kord) }

    @Suppress("EXPERIMENTAL_API_USAGE")
    val members: Flow<Member>
        get() = query<UserData>().asFlow().flatMapConcat { userData ->
            query<MemberData> { MemberData::userId eq userData.id }
                    .asFlow().map { Member(it, userData, kord) }
        }

    override suspend fun getChannel(id: Snowflake): Channel? {
        val data = query<ChannelData> { ChannelData::id eq id.longValue }.singleOrNull() ?: return null
        return Channel.from(data, kord)
    }

    override suspend fun getGuild(id: Snowflake): Guild? {
        val data = query<GuildData> { GuildData::id eq id.longValue }.singleOrNull() ?: return null
        return Guild(data, kord)
    }

    override suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member? {
        val userData = query<UserData> { UserData::id eq userId.longValue }.singleOrNull() ?: return null

        val memberData = query<MemberData> {
            MemberData::userId eq userId.longValue
            MemberData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null

        return Member(memberData, userData, kord)
    }

    override suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message? {
        val data = query<MessageData> { MessageData::id eq messageId.longValue }.singleOrNull() ?: return null

        return Message(data, kord)
    }

    override suspend fun getRole(guildId: Snowflake, roleId: Snowflake): Role? {
        val data = query<RoleData> {
            RoleData::id eq roleId.longValue
            RoleData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null

        return Role(data, kord)
    }

    suspend fun getRole(id: Snowflake): Role? {
        val data = query<RoleData> { RoleData::id eq id.longValue }.singleOrNull() ?: return null

        return Role(data, kord)
    }

    override suspend fun getSelf(): User? = getUser(kord.selfId)

    override suspend fun getUser(id: Snowflake): User? {
        val data = query<UserData> { UserData::id eq id.longValue }.singleOrNull() ?: return null

        return User(data, kord)
    }

}

class KordCacheBuilder {

    /**
     * The default behavior for all types not explicitly configured, by default a [ConcurrentHashMap] is supplied.
     */
    var defaultGenerator: Generator<Any, Any> = { cache, description ->
        MapEntryCache(cache, description, MapLikeCollection.fromThreadSafe(ConcurrentHashMap()))
    }

    private val descriptionGenerators: MutableMap<DataDescription<*, *>, Generator<*, *>> = mutableMapOf()

    /**
     * Disables caching for all entries, clearing all custom generators and setting the default to [none].
     */
    fun disableAll() {
        descriptionGenerators.clear()
        defaultGenerator = none()
    }

    /**
     * A Generator creating [DataEntryCaches][DataEntryCache] that won't store any entities, can be used to disable caching.
     */
    fun <T : Any, I : Any> none(): Generator<T, I> = { _, _ -> DataEntryCache.none() }

    /**
     * A Generator creating [DataEntryCaches][DataEntryCache] with a maximum [size], removing items on last insertion.
     * Shortcut for [lruLinkedHashMap].
     */
    fun <T : Any, I : Any> lruCache(size: Int = 100): Generator<T, I> = { cache, description ->
        MapEntryCache(cache, description, MapLikeCollection.lruLinkedHashMap(size))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any, I : Any> forDescription(description: DataDescription<T, I>, generator: Generator<T, I>?) {
        if (generator == null) {
            descriptionGenerators.remove(description)
            return
        }
        descriptionGenerators[description] = generator as Generator<*, *>
    }

    /**
     *  Configures the caching for [MessageData].
     *  Your application will generally handle a lot of messages during its lifetime, as such it's advised to
     *  limit the amount of messages cached in some way.
     *
     *  ```kotlin
     *  cache {
     *      messages(none()) //disable caching entirely
     *      messages(lruCache(100)) //only keep the latest 100 messages
     *  }
     *  ```
     */
    fun messages(generator: Generator<MessageData, Long>) = forDescription(MessageData.description, generator)

    /**
     *  Configures the caching for [RoleData].
     */
    fun roles(generator: Generator<RoleData, Long>) = forDescription(RoleData.description, generator)

    /**
     *  Configures the caching for [ChannelData].
     */
    fun channels(generator: Generator<ChannelData, Long>) = forDescription(ChannelData.description, generator)

    /**
     *  Configures the caching for [GuildData].
     */
    fun guilds(generator: Generator<GuildData, Long>) = forDescription(GuildData.description, generator)

    /**
     *  Configures the caching for [MemberData].
     *  It's advised to configure user and member data similarly, so that every member in cache also has its user data cached.
     *  Failing to do so would result in a performance hit when fetching members.
     */
    fun members(generator: Generator<MemberData, String>) = forDescription(MemberData.description, generator)

    /**
     *  Configures the caching for [UserData].
     *  It's advised to configure user and member data similarly, so that every member in cache also has its user data cached.
     *  Failing to do so would result in a performance hit when fetching members.
     */
    fun users(generator: Generator<UserData, Long>) = forDescription(UserData.description, generator)


    /**
     *  Configures the caching for [EmojiData].
     */
    fun emojis(generator: Generator<EmojiData, Long>) = forDescription(EmojiData.description, generator)

    /**
     *  Configures the caching for [WebhookData].
     */
    fun webhooks(generator: Generator<WebhookData, Long>) = forDescription(WebhookData.description, generator)

    /**
     *  Configures the caching for [PresenceData].
     */
    fun presences(generator: Generator<PresenceData, String>) = forDescription(PresenceData.description, generator)

    /**
     *  Configures the caching for [VoiceStateData].
     */
    fun voiceState(generator: Generator<VoiceStateData, String>) = forDescription(VoiceStateData.description, generator)

    fun build(): DataCache = DelegatingDataCache(EntrySupplier.invoke { cache, description ->
        val generator = descriptionGenerators[description] ?: defaultGenerator
        generator(cache, description)
    })

}
