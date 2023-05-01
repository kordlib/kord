package dev.kord.core.cache.data

import dev.kord.cache.api.observables.Cache
import dev.kord.common.entity.Snowflake

public interface TypedCache {
    public fun bans(): Cache<Snowflake, BanData>
    public fun members(): Cache<Snowflake, MemberData>
    public fun users(): Cache<Snowflake, UserData>
    public fun channels(): Cache<Snowflake, ChannelData>
    public fun guilds(): Cache<Snowflake, GuildData>
    public fun regions(): Cache<Snowflake, RegionData>
    public fun roles(): Cache<Snowflake, RoleData>
    public fun guildPreviews(): Cache<Snowflake, GuildPreviewData>
    public fun messages(): Cache<Snowflake, MessageData>
    public fun emojis(): Cache<Snowflake, EmojiData>
    public fun webhooks(): Cache<Snowflake, WebhookData>
    public fun templates(): Cache<String, TemplateData>
    public fun threadMembers(): Cache<Snowflake, ThreadMemberData>
    public fun applicationCommands(): Cache<Snowflake, ApplicationCommandData>
    public fun applicationCommandPermissions(): Cache<Snowflake, GuildApplicationCommandPermissionsData>
    public fun stickers(): Cache<Snowflake, StickerData>
    public fun autoModerationRules(): Cache<Snowflake, AutoModerationRuleData>
    public fun guildScheduledEvents(): Cache<Snowflake, GuildScheduledEventData>
    public fun stickerPacks(): Cache<Snowflake, StickerPackData>
}

