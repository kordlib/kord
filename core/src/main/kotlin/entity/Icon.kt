package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.rest.Image
import dev.kord.rest.route.CdnUrl
import dev.kord.rest.route.DiscordCdn

public sealed class Icon(
    public val format: Image.Format,
    public val cdnUrl: CdnUrl,
    override val kord: Kord
) : KordObject {

    public val url: String
        get() = cdnUrl.toUrl {
            this.format = this@Icon.format
        }

    public suspend fun getImage(): Image = Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl())

    public suspend fun getImage(size: Image.Size): Image =
        Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl {
            this.size = size
        })

    public suspend fun getImage(format: Image.Format): Image =
        Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl {
            this.format = format
        })

    public suspend fun getImage(format: Image.Format, size: Image.Size): Image =
        Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl {
            this.format = format
            this.size = size
        })

    override fun toString(): String {
        return "Icon(type=${javaClass.name},format=$format,cdnUrl=$cdnUrl,kord=$kord)"
    }

    public class EmojiIcon(animated: Boolean, emojiId: Snowflake, kord: Kord) :
        Icon(if (animated) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.emoji(emojiId), kord)

    public class DefaultUserAvatar(discriminator: Int, kord: Kord) :
        Icon(Image.Format.PNG /* Discord Default Avatars only support PNG */, DiscordCdn.defaultAvatar(discriminator), kord)

    public class UserAvatar(userId: Snowflake, avatarHash: String, kord: Kord) :
        Icon(if (avatarHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.userAvatar(userId, avatarHash), kord)

    public class MemberAvatar(guildId: Snowflake, userId: Snowflake, avatarHash: String, kord: Kord) :
        Icon(if (avatarHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.memberAvatar(guildId, userId, avatarHash), kord)

    public class RoleIcon(roleId: Snowflake, iconHash: String, kord: Kord) :
        Icon(if (iconHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.roleIcon(roleId, iconHash), kord)

}
