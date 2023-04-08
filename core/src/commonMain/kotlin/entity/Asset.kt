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
        isAnimated: Boolean,
        cdnUrl: CdnUrl,
        kord: Kord,
    ) : this(format = if (isAnimated) Image.Format.GIF else Image.Format.WEBP, cdnUrl, kord)

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
        private val String.isAnimated get() = startsWith("a_")

        public fun emoji(emojiId: Snowflake, isAnimated: Boolean, kord: Kord): Asset =
            Asset(isAnimated, DiscordCdn.emoji(emojiId), kord)

        public fun guildIcon(guildId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(hash.isAnimated, DiscordCdn.guildIcon(guildId, hash), kord)

        public fun guildSplash(guildId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(isAnimated = false, DiscordCdn.guildSplash(guildId, hash), kord)

        public fun guildDiscoverySplash(guildId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(isAnimated = false, DiscordCdn.guildDiscoverySplash(guildId, hash), kord)

        public fun guildBanner(guildId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(hash.isAnimated, DiscordCdn.guildBanner(guildId, hash), kord)

        public fun userBanner(userId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(hash.isAnimated, DiscordCdn.userBanner(userId, hash), kord)

        public fun defaultUserAvatar(discriminator: Int, kord: Kord): Asset =
            Asset(isAnimated = false, DiscordCdn.defaultAvatar(discriminator), kord)

        public fun userAvatar(userId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(hash.isAnimated, DiscordCdn.userAvatar(userId, hash), kord)

        public fun memberAvatar(guildId: Snowflake, userId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(hash.isAnimated, DiscordCdn.memberAvatar(guildId, userId, hash), kord)

        public fun applicationIcon(applicationId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(isAnimated = false, DiscordCdn.applicationIcon(applicationId, hash), kord)

        public fun applicationCover(applicationId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(isAnimated = false, DiscordCdn.applicationCover(applicationId, hash), kord)

        public fun stickerPackBanner(bannerAssetId: Snowflake, kord: Kord): Asset =
            Asset(isAnimated = false, DiscordCdn.stickerPackBanner(bannerAssetId), kord)

        public fun teamIcon(teamId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(isAnimated = false, DiscordCdn.teamIcon(teamId, hash), kord)

        public fun roleIcon(roleId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(isAnimated = false, DiscordCdn.roleIcon(roleId, hash), kord)

        public fun guildScheduledEventCover(eventId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(isAnimated = false, DiscordCdn.guildScheduledEventCover(eventId, hash), kord)

        public fun memberBanner(guildId: Snowflake, userId: Snowflake, hash: String, kord: Kord): Asset =
            Asset(hash.isAnimated, DiscordCdn.memberBanner(guildId, userId, hash), kord)
    }
}
