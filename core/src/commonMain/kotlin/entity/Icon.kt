package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.rest.Image
import dev.kord.rest.route.CdnUrl
import dev.kord.rest.route.DiscordCdn

/**
 * Represents an Icon image.
 *
 * @param format The [Image.Format] for the Icon
 * @param animated Whether the Icon is animated orn ot
 * @param cdnUrl The [URL][CdnUrl] for the icon
 */
@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "Icon class does not cover all cdn endpoints and has some inconsistencies.",
    ReplaceWith("Asset", "dev.kord.core.entity.Asset"),
    DeprecationLevel.HIDDEN
)
public sealed class Icon(
    public val format: Image.Format,
    public val animated: Boolean,
    public val cdnUrl: CdnUrl,
    override val kord: Kord
) : KordObject {
    /** The [cdnUrl] formatted as a regular URL. */
    public val url: String
        get() = cdnUrl.toUrl {
            this.format = this@Icon.format
        }

    /** Returns the icon as an [Image] from the [cdnUrl]. */
    public suspend fun getImage(): Image = Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl())

    /**
     * Returns the icon as an [Image] from the [cdnUrl] with a specified [size].
     *
     * @param size A custom [Image.Size] for the image.
     */
    public suspend fun getImage(size: Image.Size): Image =
        Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl {
            this.size = size
        })

    /**
     * Returns the icon as an [Image] from the [cdnUrl] with a specified [format].
     *
     * @param format A custom [Image.Format] for the image.
     */
    public suspend fun getImage(format: Image.Format): Image =
        Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl {
            this.format = format
        })

    /**
     * Returns the icon as an [Image] from the [cdnUrl] with a specified [format] and [size].
     *
     * @param format A custom [Image.Format] for the image.
     * @param size A custom [Image.Size] for the image.
     */
    public suspend fun getImage(format: Image.Format, size: Image.Size): Image =
        Image.fromUrl(kord.resources.httpClient, cdnUrl.toUrl {
            this.format = format
            this.size = size
        })

    override fun toString(): String {
        return "Icon(type=${this::class.simpleName},format=$format,animated=$animated,cdnUrl=$cdnUrl,kord=$kord)"
    }

    /**
     * Represents an [Icon] as an Emoji icon
     *
     * @param animated Whether the emoji is animated or not
     * @param emojiId The ID of the emoji
     * @param kord The Kord instance that created this object
     */
    public class EmojiIcon(animated: Boolean, emojiId: Snowflake, kord: Kord) :
        Icon(if (animated) Image.Format.GIF else Image.Format.WEBP, animated, DiscordCdn.emoji(emojiId), kord)

    public class DefaultUserAvatar private constructor(cdnUrl: CdnUrl, kord: Kord) :
        Icon(Image.Format.PNG /* Discord Default Avatars only support PNG */, false, cdnUrl, kord) {
        @Suppress("DEPRECATION")
        public constructor(discriminator: Int, kord: Kord) : this(DiscordCdn.defaultAvatar(discriminator), kord)
        internal constructor(userId: Snowflake, kord: Kord) : this(DiscordCdn.defaultUserAvatar(userId), kord)
    }

    /**
     * Represents an [Icon] as a User Avatar
     *
     * @param userId The ID of the user
     * @param avatarHash The hash for the users avatar
     * @param kord The Kord instance that created this object
     */
    public class UserAvatar(userId: Snowflake, avatarHash: String, kord: Kord) :
        Icon(if (avatarHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, avatarHash.startsWith("a_"), DiscordCdn.userAvatar(userId, avatarHash), kord)

    /**
     * Represents an [Icon] as a Member avatar
     *
     * @param guildId The ID of the guild the user is a member of
     * @param userId The ID of the user
     * @param avatarHash The hash for the users avatar
     * @param kord The Kord instance that created this object
     */
    public class MemberAvatar(guildId: Snowflake, userId: Snowflake, avatarHash: String, kord: Kord) :
        Icon(if (avatarHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, avatarHash.startsWith("a_"), DiscordCdn.memberAvatar(guildId, userId, avatarHash), kord)

    /**
     * Represents an [Icon] as a role icon
     *
     * @param roleId The ID of the role the icon is for
     * @param iconHash The hash for the icon
     * @param kord The Kord instance that created this object
     */
    public class RoleIcon(roleId: Snowflake, iconHash: String, kord: Kord) :
        Icon(if (iconHash.startsWith("a_")) Image.Format.GIF else Image.Format.WEBP, iconHash.startsWith("a_"), DiscordCdn.roleIcon(roleId, iconHash), kord)

}
