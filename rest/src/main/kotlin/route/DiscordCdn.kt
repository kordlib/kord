package dev.kord.rest.route

import dev.kord.common.entity.Snowflake

object DiscordCdn {

    private const val BASE_URL = "https://cdn.discordapp.com"

    fun emoji(emojiId: Snowflake): CdnUrlBuilder = CdnUrlBuilder("$BASE_URL/emojis/${emojiId.asString}")

    fun defaultAvatar(discriminator: Int): CdnUrlBuilder = CdnUrlBuilder("$BASE_URL/embed/avatars/${discriminator % 5}")

    fun userAvatar(userId: Snowflake, hash: String): CdnUrlBuilder = CdnUrlBuilder("$BASE_URL/avatars/${userId.asString}/$hash")

    fun memberAvatar(guildId: Snowflake, userId: Snowflake, hash: String) =
        CdnUrlBuilder("$BASE_URL/guilds/${guildId.asString}/users/${userId.asString}/avatars/$hash")

}