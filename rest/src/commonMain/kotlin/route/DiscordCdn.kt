package dev.kord.rest.route

import dev.kord.common.entity.Snowflake

public object DiscordCdn {
    // see https://discord.com/developers/docs/reference#image-formatting-cdn-endpoints

    private const val BASE_URL = "https://cdn.discordapp.com"

    public fun emoji(emojiId: Snowflake): CdnUrl = CdnUrl("$BASE_URL/emojis/$emojiId")

    public fun guildIcon(guildId: Snowflake, hash: String): CdnUrl = CdnUrl("$BASE_URL/icons/$guildId/$hash")

    public fun guildSplash(guildId: Snowflake, hash: String): CdnUrl = CdnUrl("$BASE_URL/splashes/$guildId/$hash")

    public fun guildDiscoverySplash(guildId: Snowflake, hash: String): CdnUrl =
        CdnUrl("$BASE_URL/discovery-splashes/$guildId/$hash")

    public fun guildBanner(guildId: Snowflake, hash: String): CdnUrl = CdnUrl("$BASE_URL/banners/$guildId/$hash")

    public fun userBanner(userId: Snowflake, hash: String): CdnUrl = CdnUrl("$BASE_URL/banners/$userId/$hash")

    public fun defaultAvatar(discriminator: Int): CdnUrl = CdnUrl("$BASE_URL/embed/avatars/${discriminator % 5}")

    public fun userAvatar(userId: Snowflake, hash: String): CdnUrl = CdnUrl("$BASE_URL/avatars/$userId/$hash")

    public fun memberAvatar(guildId: Snowflake, userId: Snowflake, hash: String): CdnUrl =
        CdnUrl("$BASE_URL/guilds/$guildId/users/$userId/avatars/$hash")

    public fun applicationIcon(applicationId: Snowflake, hash: String): CdnUrl =
        CdnUrl("$BASE_URL/app-icons/$applicationId/$hash")

    public fun applicationCover(applicationId: Snowflake, hash: String): CdnUrl =
        CdnUrl("$BASE_URL/app-icons/$applicationId/$hash")

    public fun stickerPackBanner(bannerAssetId: Snowflake): CdnUrl =
        CdnUrl("$BASE_URL/app-assets/710982414301790216/store/$bannerAssetId")

    public fun teamIcon(teamId: Snowflake, hash: String): CdnUrl = CdnUrl("$BASE_URL/team-icons/$teamId/$hash")

    public fun sticker(stickerId: Snowflake): CdnUrl = CdnUrl("$BASE_URL/stickers/$stickerId")

    public fun roleIcon(roleId: Snowflake, hash: String): CdnUrl = CdnUrl("$BASE_URL/role-icons/$roleId/$hash")

    public fun guildScheduledEventCover(eventId: Snowflake, hash: String): CdnUrl =
        CdnUrl("$BASE_URL/guild-events/$eventId/$hash")

    public fun memberBanner(guildId: Snowflake, userId: Snowflake, hash: String): CdnUrl =
        CdnUrl("$BASE_URL/guilds/$guildId/users/$userId/banners/$hash")
}
