package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.rest.Image
import dev.kord.rest.route.CdnUrl
import dev.kord.rest.route.DiscordCdn

public sealed class Icon(
    public val format: Image.Format,
    public val animated: Boolean,
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
        return "Icon(type=${javaClass.name},format=$format,animated=$animated,cdnUrl=$cdnUrl,kord=$kord)"
    }

    @Deprecated(
        "Icon class does not cover all CDN endpoints",
        ReplaceWith("Asset.Emoji"),
        DeprecationLevel.WARNING
    )
    public class EmojiIcon(animated: Boolean, emojiId: Snowflake, kord: Kord) :
        Icon(if (animated) Image.Format.GIF else Image.Format.WEBP, animated, DiscordCdn.emoji(emojiId), kord)

    @Deprecated(
        "Icon class does not cover all CDN endpoints",
        ReplaceWith("Asset.DefaultUserAvatar"),
        DeprecationLevel.WARNING
    )
    public class DefaultUserAvatar(discriminator: Int, kord: Kord) :
        Icon(
            Image.Format.PNG /* Discord Default Avatars only support PNG */,
            false,
            DiscordCdn.defaultAvatar(discriminator),
            kord
        )

    @Deprecated(
        "Icon class does not cover all CDN endpoints",
        ReplaceWith("Asset.UserAvatar"),
        DeprecationLevel.WARNING
    )
    public class UserAvatar(userId: Snowflake, avatarHash: String, kord: Kord) :
        Icon(
            if (avatarHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP,
            avatarHash.startsWith("a_"),
            DiscordCdn.userAvatar(userId, avatarHash),
            kord
        )

    @Deprecated(
        "Icon class does not cover all CDN endpoints",
        ReplaceWith("Asset.MemberAvatar"),
        DeprecationLevel.WARNING
    )
    public class MemberAvatar(guildId: Snowflake, userId: Snowflake, avatarHash: String, kord: Kord) :
        Icon(
            if (avatarHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP,
            avatarHash.startsWith("a_"),
            DiscordCdn.memberAvatar(guildId, userId, avatarHash),
            kord
        )

    @Deprecated(
        "Icon class does not cover all CDN endpoints",
        ReplaceWith("Asset.RoleIcon"),
        DeprecationLevel.WARNING
    )
    public class RoleIcon(roleId: Snowflake, iconHash: String, kord: Kord) :
        Icon(
            if (iconHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP,
            iconHash.startsWith("a_"),
            DiscordCdn.roleIcon(roleId, iconHash),
            kord
        )
}