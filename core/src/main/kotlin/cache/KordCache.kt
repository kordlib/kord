package dev.kord.core.cache

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.DataEntryCache
import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.delegate.DelegatingDataCache
import dev.kord.cache.api.delegate.EntrySupplier
import dev.kord.cache.map.MapLikeCollection
import dev.kord.cache.map.internal.MapEntryCache
import dev.kord.cache.map.lruLinkedHashMap
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.*
import java.util.concurrent.ConcurrentHashMap

public typealias Generator<I, T> = (cache: DataCache, description: DataDescription<T, I>) -> DataEntryCache<out T>

public class KordCacheBuilder {

    /**
     * The default behavior for all types not explicitly configured, by default a [ConcurrentHashMap] is supplied.
     */
    public var defaultGenerator: Generator<Any, Any> = { cache, description ->
        MapEntryCache(cache, description, MapLikeCollection.concurrentHashMap())
    }

    private val descriptionGenerators: MutableMap<DataDescription<*, *>, Generator<*, *>> = mutableMapOf()

    init {
        messages { _, _ -> DataEntryCache.none() }
    }

    /**
     * Disables caching for all entries, clearing all custom generators and setting the default to [none].
     */
    public fun disableAll() {
        descriptionGenerators.clear()
        defaultGenerator = none()
    }

    /**
     * A Generator creating [DataEntryCaches][DataEntryCache] that won't store any entities, can be used to disable caching.
     */
    public fun <T : Any, I : Any> none(): Generator<T, I> = { _, _ -> DataEntryCache.none() }

    /**
     * A Generator creating [DataEntryCaches][DataEntryCache] with a maximum [size], removing items on last insertion.
     * Shortcut for [lruLinkedHashMap].
     */
    public fun <T : Any, I : Any> lruCache(size: Int = 100): Generator<T, I> = { cache, description ->
        MapEntryCache(cache, description, MapLikeCollection.lruLinkedHashMap(size))
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T : Any, I : Any> forDescription(description: DataDescription<T, I>, generator: Generator<T, I>?) {
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
    public fun messages(generator: Generator<MessageData, Snowflake>): Unit = forDescription(MessageData.description, generator)

    /**
     *  Configures the caching for [RoleData].
     */
    public fun roles(generator: Generator<RoleData, Snowflake>): Unit = forDescription(RoleData.description, generator)

    /**
     *  Configures the caching for [ChannelData].
     */
    public fun channels(generator: Generator<ChannelData, Snowflake>): Unit = forDescription(ChannelData.description, generator)

    /**
     *  Configures the caching for [GuildData].
     */
    public fun guilds(generator: Generator<GuildData, Snowflake>): Unit = forDescription(GuildData.description, generator)

    /**
     *  Configures the caching for [MemberData].
     *  It's advised to configure user and member data similarly, so that every member in cache also has its user data cached.
     *  Failing to do so would result in a performance hit when fetching members.
     */
    public fun members(generator: Generator<MemberData, String>): Unit = forDescription(MemberData.description, generator)

    /**
     *  Configures the caching for [UserData].
     *  It's advised to configure user and member data similarly, so that every member in cache also has its user data cached.
     *  Failing to do so would result in a performance hit when fetching members.
     */
    public fun users(generator: Generator<UserData, Snowflake>): Unit = forDescription(UserData.description, generator)

    public fun stickers(generator: Generator<StickerData, Snowflake>): Unit = forDescription(StickerData.description, generator)


    /**
     *  Configures the caching for [EmojiData].
     */
    public fun emojis(generator: Generator<EmojiData, Snowflake>): Unit = forDescription(EmojiData.description, generator)

    /**
     *  Configures the caching for [WebhookData].
     */
    public fun webhooks(generator: Generator<WebhookData, Snowflake>): Unit = forDescription(WebhookData.description, generator)

    /**
     *  Configures the caching for [PresenceData].
     */
    public fun presences(generator: Generator<PresenceData, String>): Unit = forDescription(PresenceData.description, generator)

    /**
     *  Configures the caching for [VoiceStateData].
     */
    public fun voiceState(generator: Generator<VoiceStateData, String>): Unit = forDescription(VoiceStateData.description, generator)

    public fun build(): DataCache = DelegatingDataCache(EntrySupplier.invoke { cache, description ->
        val generator = descriptionGenerators[description] ?: defaultGenerator
        generator(cache, description)
    })

}
