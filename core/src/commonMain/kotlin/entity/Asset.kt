package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.rest.Image
import dev.kord.rest.route.CdnUrl
import dev.kord.rest.route.DiscordCdn

public class Asset(
    public val format: Image.Format,
    public val cdnUrl: CdnUrl,
    override val kord: Kord,
) : KordObject {
    public constructor(
        animated: Boolean,
        cdnUrl: CdnUrl,
        kord: Kord,
    ) : this(format = if (animated) Image.Format.GIF else Image.Format.WEBP, cdnUrl, kord)

    public val url: String
        get() = cdnUrl.toUrl {
            this.format = this@Asset.format
        }

    public val animated: Boolean get() = format is Image.Format.GIF

    public suspend fun getImage(format: Image.Format = this.format, size: Image.Size? = null): Image = Image.fromUrl(
        client = kord.resources.httpClient,
        url = cdnUrl.toUrl {
            this.format = format
            if (size != null) this.size = size
        },
    )

    public companion object {
        public fun emoji(animated: Boolean, emojiId: Snowflake, kord: Kord): Asset =
            Asset(if (animated) Image.Format.GIF else Image.Format.WEBP, DiscordCdn.emoji(emojiId), kord)

        public fun defaultUserAvatar(discriminator: Int, kord: Kord): Asset =
            Asset(false, DiscordCdn.defaultAvatar(discriminator), kord)

        public fun userAvatar(userId: Snowflake, avatarHash: String, kord: Kord): Asset =
            Asset(
                avatarHash.startsWith("a_"),
                DiscordCdn.userAvatar(userId, avatarHash),
                kord
            )

        public fun userBanner(userId: Snowflake, bannerHash: String, kord: Kord): Asset =
            Asset(
                bannerHash.startsWith("a_"),
                DiscordCdn.userBanner(userId, bannerHash),
                kord
            )

        public fun memberAvatar(guildId: Snowflake, userId: Snowflake, avatarHash: String, kord: Kord): Asset =
            Asset(
                avatarHash.startsWith("a_"),
                DiscordCdn.memberAvatar(guildId, userId, avatarHash),
                kord
            )

        public fun memberBanner(guildId: Snowflake, userId: Snowflake, bannerHash: String, kord: Kord): Asset =
            Asset(
                bannerHash.startsWith("a_"),
                DiscordCdn.memberBanner(guildId, userId, bannerHash),
                kord
            )

        public fun roleIcon(roleId: Snowflake, iconHash: String, kord: Kord): Asset =
            Asset(false, DiscordCdn.roleIcon(roleId, iconHash), kord)

        public fun guildIcon(guildId: Snowflake, iconHash: String, kord: Kord): Asset =
            Asset(
                iconHash.startsWith("a_"),
                DiscordCdn.guildIcon(guildId, iconHash),
                kord
            )

        public fun guildSplash(guildId: Snowflake, splashHash: String, kord: Kord): Asset =
            Asset(false, DiscordCdn.guildSplash(guildId, splashHash), kord)

        public fun guildDiscoverySplash(guildId: Snowflake, splashHash: String, kord: Kord): Asset =
            Asset(false, DiscordCdn.guildDiscoverySplash(guildId, splashHash), kord)

        public fun guildBanner(guildId: Snowflake, bannerHash: String, kord: Kord): Asset =
            Asset(false, DiscordCdn.guildBanner(guildId, bannerHash), kord)

        public fun guildEventCover(guildId: Snowflake, coverHash: String, kord: Kord): Asset =
            Asset(false, DiscordCdn.guildEventCover(guildId, coverHash), kord)

        public fun applicationIcon(applicationId: Snowflake, iconHash: String, kord: Kord): Asset =
            Asset(false, DiscordCdn.applicationIcon(applicationId, iconHash), kord)

        public fun applicationCover(applicationId: Snowflake, coverHash: String, kord: Kord): Asset =
            Asset(false, DiscordCdn.applicationCover(applicationId, coverHash), kord)

        public fun teamIcon(teamId: Snowflake, iconHash: String, kord: Kord): Asset =
            Asset(false, DiscordCdn.teamIcon(teamId, iconHash), kord)

        public fun stickerPackBanner(stickerPackId: Snowflake, kord: Kord): Asset =
            Asset(false, DiscordCdn.stickerPackBanner(stickerPackId), kord)
    }
}
