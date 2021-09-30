package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.rest.Image
import dev.kord.rest.route.CDNUrl
import dev.kord.rest.route.DiscordCDN

sealed class Icon(val animated: Boolean, val cdnUrl: CDNUrl, override val kord: Kord) : KordObject {

    val format: Image.Format
        get() = when {
            animated -> Image.Format.GIF
            else -> Image.Format.WEBP
        }

    val url: String
        get() = cdnUrl.toUrl {
            this.format = this@Icon.format
        }

    suspend fun getImage(): Image = Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl())

    suspend fun getImage(size: Image.Size): Image =
        Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl {
            this.size = size
        })

    suspend fun getImage(format: Image.Format): Image =
        Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl {
            this.format = format
        })

    suspend fun getImage(format: Image.Format, size: Image.Size): Image =
        Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl {
            this.format = format
            this.size = size
        })

    override fun toString(): String {
        return "Icon(type=${javaClass.name},animated=$animated,cdnUrl=$cdnUrl,kord=$kord)"
    }

    class EmojiIcon(animated: Boolean, emojiId: Snowflake, kord: Kord) : Icon(animated, DiscordCDN.emoji(emojiId), kord)

    class DefaultUserAvatar(discriminator: Int, kord: Kord) : Icon(false, DiscordCDN.defaultAvatar(discriminator), kord)

    class UserAvatar(userId: Snowflake, avatarHash: String, kord: Kord) :
        Icon(avatarHash.startsWith("a_"), DiscordCDN.userAvatar(userId, avatarHash), kord)

    class MemberAvatar(guildId: Snowflake, userId: Snowflake, avatarHash: String, kord: Kord) :
        Icon(avatarHash.startsWith("a_"), DiscordCDN.memberAvatar(guildId, userId, avatarHash), kord)

    class RoleIcon(roleId: Snowflake, iconHash: String, kord: Kord) :
        Icon(iconHash.startsWith("a_"), DiscordCDN.roleIcon(roleId, iconHash), kord)
}