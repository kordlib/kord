package dev.kord.core.cache

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.query
import dev.kord.common.annotation.KordInternal
import dev.kord.core.cache.data.*

/**
 * Registers all Kord data classes for this cache
 *
 * @suppress
 */
@KordInternal
public suspend fun DataCache.registerKordData(): Unit = register(
    RoleData.description,
    ChannelData.description,
    GuildData.description,
    MemberData.description,
    UserData.description,
    ThreadMemberData.description,
    MessageData.description,
    EmojiData.description,
    WebhookData.description,
    PresenceData.description,
    VoiceStateData.description,
    ApplicationCommandData.description,
    GuildApplicationCommandPermissionsData.description,
    StickerPackData.description,
    StickerData.description,
    AutoModerationRuleData.description,
)

/**
 * Removes all cached Kord data instances from this cache
 */
internal suspend fun DataCache.removeKordData() {
    query<RoleData>().remove()
    query<ChannelData>().remove()
    query<GuildData>().remove()
    query<MemberData>().remove()
    query<UserData>().remove()
    query<ThreadMemberData>().remove()
    query<MessageData>().remove()
    query<EmojiData>().remove()
    query<WebhookData>().remove()
    query<PresenceData>().remove()
    query<VoiceStateData>().remove()
    query<ApplicationCommandData>().remove()
    query<GuildApplicationCommandPermissionsData>().remove()
    query<StickerPackData>().remove()
    query<StickerData>().remove()
    query<AutoModerationRuleData>().remove()
}

/**
 * Creates a [DataCacheView] for this view, only removing elements that were added
 * directly to this instance.
 */
public suspend fun DataCache.createView(): DataCacheView = DataCacheView(this)
