package com.gitlab.kordlib.core.shard

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.core.*

/**
 * Registers all Kord data classes for this cache
 */
internal suspend fun DataCache.registerKordData() = register(
        RoleData.description,
        ChannelData.description,
        GuildData.description,
        GuildMemberData.description,
        UserData.description,
        MessageData.description,
        EmojiData.description,
        WebhookData.description
)

/**
 * Removes all cached Kord data instances from this cache
 */
internal suspend fun DataCache.removeKordData() {
    find<RoleData>().remove()
    find<ChannelData>().remove()
    find<GuildData>().remove()
    find<GuildMemberData>().remove()
    find<UserData>().remove()
    find<MessageData>().remove()
    find<EmojiData>().remove()
    find<WebhookData>().remove()
}

/**
 * Creates a [DataCacheView] for this view, only removing elements that were added
 * directly to this instance.
 */
suspend fun DataCache.createView() : DataCacheView = DataCacheView(this)