package dev.kord.rest.route

import dev.kord.common.entity.Snowflake

object DiscordCdn {

    private const val BASE_URL = "https://cdn.discordapp.com"

    fun emoji(emojiId: Snowflake): CdnUrl = CdnUrl("$BASE_URL/emojis/${emojiId.asString}")

    fun defaultAvatar(discriminator: Int): CdnUrl = CdnUrl("$BASE_URL/embed/avatars/${discriminator % 5}")

    fun userAvatar(userId: Snowflake, hash: String): CdnUrl = CdnUrl("$BASE_URL/avatars/${userId.asString}/$hash")

    fun memberAvatar(guildId: Snowflake, userId: Snowflake, hash: String) =
        CdnUrl("$BASE_URL/guilds/${guildId.asString}/users/${userId.asString}/avatars/$hash")

    fun roleIcon(roleId: Snowflake, hash: String) = CdnUrl("$BASE_URL/role-icons/${roleId.asString}/$hash")
}