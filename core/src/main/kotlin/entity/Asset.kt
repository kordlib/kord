package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.rest.Image
import dev.kord.rest.route.CdnUrl
import dev.kord.rest.route.DiscordCdn

public sealed class Asset(
    public val format: Image.Format,
    public val cdnUrl: CdnUrl,
    override val kord: Kord
) : KordObject {
    public val animated: Boolean get() = format is Image.Format.GIF

    public val url: String
        get() = cdnUrl.toUrl {
            this.format = this@Asset.format
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

    public class EmojiIcon(animated: Boolean, emojiId: Snowflake, kord: Kord) : Asset(if (animated) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.emoji(emojiId), kord)

    public class DefaultUserAvatar(discriminator: Int, kord: Kord) :
        Asset(Image.Format.PNG /* Discord Default Avatars only support PNG */, DiscordCdn.defaultAvatar(discriminator), kord)

    public class UserAvatar(userId: Snowflake, avatarHash: String, kord: Kord) :
        Asset(if (avatarHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.userAvatar(userId, avatarHash), kord)

    public class UserBanner(userId: Snowflake, bannerHash: String, kord: Kord) :
        Asset(if (bannerHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.userBanner(userId, bannerHash), kord)

    public class MemberAvatar(guildId: Snowflake, userId: Snowflake, avatarHash: String, kord: Kord) :
        Asset(if (avatarHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.memberAvatar(guildId, userId, avatarHash), kord)

    public class MemberBanner(guildId: Snowflake, userId: Snowflake, bannerHash: String, kord: Kord) :
        Asset(if (bannerHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.memberBanner(guildId, userId, bannerHash), kord)

    public class RoleIcon(roleId: Snowflake, iconHash: String, kord: Kord) :
        Asset(Image.Format.PNG, DiscordCdn.roleIcon(roleId, iconHash), kord)

    public class GuildIcon(guildId: Snowflake, iconHash: String, kord: Kord) :
        Asset(if (iconHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.guildIcon(guildId, iconHash), kord)

    public class GuildSplash(guildId: Snowflake, splashHash: String, kord: Kord) :
        Asset(Image.Format.PNG, DiscordCdn.guildSplash(guildId, splashHash), kord)

    public class GuildDiscoverySplash(guildId: Snowflake, splashHash: String, kord: Kord) :
        Asset(Image.Format.PNG, DiscordCdn.guildDiscoverySplash(guildId, splashHash), kord)

    public class GuildBanner(guildId: Snowflake, bannerHash: String, kord: Kord) :
        Asset(if (bannerHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.guildBanner(guildId, bannerHash), kord)

    public class GuildEventCover(guildId: Snowflake, coverHash: String, kord: Kord) :
        Asset(Image.Format.PNG, DiscordCdn.guildEventCover(guildId, coverHash), kord)

    public class ApplicationIcon(applicationId: Snowflake, iconHash: String, kord: Kord) :
        Asset(Image.Format.PNG, DiscordCdn.applicationIcon(applicationId, iconHash), kord)

    public class ApplicationCover(applicationId: Snowflake, coverHash: String, kord: Kord) :
        Asset(Image.Format.PNG, DiscordCdn.applicationCover(applicationId, coverHash), kord)

    public class TeamIcon(teamId: Snowflake, iconHash: String, kord: Kord) :
        Asset(Image.Format.PNG, DiscordCdn.teamIcon(teamId, iconHash), kord)

    public class StickerPackBanner(assetId: Snowflake, kord: Kord) :
        Asset(Image.Format.PNG, DiscordCdn.stickerPackBanner(assetId), kord)

    override fun toString(): String {
        return "${javaClass.name}(format=$format,cdnUrl=$cdnUrl,kord=$kord)"
    }
}