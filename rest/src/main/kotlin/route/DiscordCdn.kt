package dev.kord.rest.route

import dev.kord.common.entity.Snowflake

public object DiscordCdn {

    private const val BASE_URL = "https://cdn.discordapp.com"

    public fun emoji(emojiId: Snowflake): CdnUrl = CdnUrl("$BASE_URL/emojis/$emojiId")

    public fun defaultAvatar(discriminator: Int): CdnUrl = CdnUrl("$BASE_URL/embed/avatars/${discriminator % 5}")

    public fun userAvatar(userId: Snowflake, hash: String): CdnUrl = CdnUrl("$BASE_URL/avatars/$userId/$hash")

    public fun memberAvatar(guildId: Snowflake, userId: Snowflake, hash: String): CdnUrl =
        CdnUrl("$BASE_URL/guilds/$guildId/users/$userId/avatars/$hash")

    public fun roleIcon(roleId: Snowflake, hash: String): CdnUrl = CdnUrl("$BASE_URL/role-icons/$roleId/$hash")
}
