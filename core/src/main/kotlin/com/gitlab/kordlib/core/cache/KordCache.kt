package com.gitlab.kordlib.core.cache

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.DataEntryCache
import com.gitlab.kordlib.cache.api.data.DataDescription
import com.gitlab.kordlib.cache.api.delegate.DelegatingDataCache
import com.gitlab.kordlib.cache.api.delegate.EntrySupplier
import com.gitlab.kordlib.cache.map.MapLikeCollection
import com.gitlab.kordlib.cache.map.internal.MapEntryCache
import com.gitlab.kordlib.cache.map.lruLinkedHashMap
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.cache.data.*
import java.util.concurrent.ConcurrentHashMap

typealias Generator<I, T> = (cache: DataCache, description: DataDescription<T, I>) -> DataEntryCache<out T>

class KordCacheBuilder {

    /**
     * The default behavior for all types not explicitly configured, by default a [ConcurrentHashMap] is supplied.
     */
    var defaultGenerator: Generator<Any, Any> = { cache, description ->
        MapEntryCache(cache, description, MapLikeCollection.concurrentHashMap())
    }

    private val descriptionGenerators: MutableMap<DataDescription<*, *>, Generator<*, *>> = mutableMapOf()

    init {
        messages { _, _ -> DataEntryCache.none() }
    }

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
        if (generator == null) return run {
            descriptionGenerators.remove(description)
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
    fun messages(generator: Generator<MessageData, Snowflake>) = forDescription(MessageData.description, generator)

    /**
     *  Configures the caching for [RoleData].
     */
    fun roles(generator: Generator<RoleData, Snowflake>) = forDescription(RoleData.description, generator)

    /**
     *  Configures the caching for [ChannelData].
     */
    fun channels(generator: Generator<ChannelData, Snowflake>) = forDescription(ChannelData.description, generator)

    /**
     *  Configures the caching for [GuildData].
     */
    fun guilds(generator: Generator<GuildData, Snowflake>) = forDescription(GuildData.description, generator)

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
    fun users(generator: Generator<UserData, Snowflake>) = forDescription(UserData.description, generator)


    /**
     *  Configures the caching for [EmojiData].
     */
    fun emojis(generator: Generator<EmojiData, Snowflake>) = forDescription(EmojiData.description, generator)

    /**
     *  Configures the caching for [WebhookData].
     */
    fun webhooks(generator: Generator<WebhookData, Snowflake>) = forDescription(WebhookData.description, generator)

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
