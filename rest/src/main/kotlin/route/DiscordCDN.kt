package dev.kord.rest.route

import dev.kord.common.entity.Snowflake
import dev.kord.rest.Image

object DiscordCDN {

    private const val BASE_URL = "https://cdn.discordapp.com"

    fun emoji(emojiId: Snowflake): CDNUrl = CDNUrl("$BASE_URL/emojis/${emojiId.asString}")

    fun defaultAvatar(discriminator: Int): CDNUrl = CDNUrl("$BASE_URL/embed/avatars/${discriminator % 5}")

    fun userAvatar(userId: Snowflake, hash: String): CDNUrl = CDNUrl("$BASE_URL/avatars/${userId.asString}/$hash")

    fun memberAvatar(guildId: Snowflake, userId: Snowflake, hash: String) =
        CDNUrl("$BASE_URL/guilds/${guildId.asString}/users/${userId.asString}/avatars/$hash")

    fun roleIcon(roleId: Snowflake, hash: String) = CDNUrl("$BASE_URL/role-icons/${roleId.asString}/$hash")
}

class CDNUrl(private val rawAssetUri: String) {

    fun toUrl(builder: UrlBuilder.() -> Unit = {}): String {
        val config = UrlBuilder().apply(builder)
        var cdnUrl = "$rawAssetUri.${config.format.extension}"
        config.size?.let { cdnUrl += "?size=${it.maxRes}" }
        return cdnUrl
    }

    override fun toString(): String {
        return "CdnUrl(rawAssetUri=$rawAssetUri)"
    }

    data class UrlBuilder(var format: Image.Format = Image.Format.WEBP, var size: Image.Size? = null)
}