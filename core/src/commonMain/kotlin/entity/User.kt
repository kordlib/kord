package dev.kord.core.entity

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.UserFlags
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.UserData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.Image
import kotlin.DeprecationLevel.ERROR
import kotlin.DeprecationLevel.WARNING

/**
 * An instance of a [Discord User](https://discord.com/developers/docs/resources/user#user-object).
 */
public open class User(
    public val data: UserData,
    override val kord: Kord, override val supplier: EntitySupplier = kord.defaultSupplier,
) : UserBehavior {

    override val id: Snowflake
        get() = data.id

    /**
     * The users avatar as [Icon] object
     */
    @Suppress("DEPRECATION_ERROR")
    @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
    public fun getAvatar(): Icon? = data.avatar?.let { Icon.UserAvatar(data.id, it, kord) }

    public val avatarHash: String? get() = data.avatar

    /** The avatar of this user as an [Asset]. */
    public val avatar: Asset? get() = avatarHash?.let { Asset.userAvatar(data.id, it, kord) }

    @Suppress("DEPRECATION_ERROR")
    @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
    public fun getDefaultAvatar(): Icon =
        if (migratedToNewUsernameSystem) Icon.DefaultUserAvatar(userId = id, kord)
        else @Suppress("DEPRECATION") Icon.DefaultUserAvatar(discriminator.toInt(), kord)

    public val defaultAvatar: Asset
        get() =
            if (migratedToNewUsernameSystem) Asset.defaultUserAvatar(userId = id, kord)
            else @Suppress("DEPRECATION") Asset.defaultUserAvatar(discriminator.toInt(), kord)

    /**
     * The username of this user.
     */
    public val username: String get() = data.username

    /**
     * The 4-digit code at the end of the user's discord tag.
     *
     * `"0"` indicates that this user has been migrated to the new username system, see the
     * [Discord Developer Documentation](https://discord.com/developers/docs/change-log#unique-usernames-on-discord) for
     * details.
     */
    // "0" when data.discriminator is missing: if the field is missing, all users were migrated,
    // see https://discord.com/developers/docs/change-log#identifying-migrated-users:
    // "After migration of all users is complete, the discriminator field may be removed."
    @Suppress("DEPRECATION", "DeprecatedCallableAddReplaceWith")
    @Deprecated(
        "Discord's username system is changing and discriminators are being removed, see " +
            "https://discord.com/developers/docs/change-log#unique-usernames-on-discord for details.",
        level = WARNING,
    )
    public val discriminator: String get() = data.discriminator.value ?: "0"

    // see https://discord.com/developers/docs/change-log#identifying-migrated-users
    @Suppress("DEPRECATION")
    private val migratedToNewUsernameSystem get() = discriminator == "0"

    /** The user's display name, if it is set. For bots, this is the application name. */
    public val globalName: String? get() = data.globalName.value

    /** The name as shown in the discord client, prioritizing [globalName] over [username]. */
    public open val displayName: String get() = globalName ?: username

    override suspend fun asUser(): User {
        return this
    }

    override suspend fun asUserOrNull(): User {
        return this
    }

    /**
     * The flags on a user's account, if present.
     */
    public val publicFlags: UserFlags? get() = data.publicFlags.value

    /**
     * The complete user tag.
     */
    @Suppress("DEPRECATION")
    public val tag: String get() = if (migratedToNewUsernameSystem) "@$username" else "$username#$discriminator"

    /**
     * Whether this user is a bot account.
     */
    public val isBot: Boolean get() = data.bot.discordBoolean

    public val accentColor: Color? get() = data.accentColor?.let { Color(it) }

    @Deprecated("Old method", ReplaceWith("this.banner?.cdnUrl?.toUrl { this@toUrl.format = format }"), level = ERROR)
    public fun getBannerUrl(format: Image.Format): String? =
        data.banner?.let { "https://cdn.discordapp.com/banners/$id/$it.${format.extension}" }

    public val bannerHash: String? get() = data.banner

    public val banner: Asset? get() = bannerHash?.let { Asset.userBanner(id, it, kord) }

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean = when (other) {
        is UserBehavior -> other.id == id
        else -> false
    }

    /**
     * Returns a new [User] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): User = User(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "User(data=$data, kord=$kord, supplier=$supplier)"
    }

    @Deprecated("Old class", ReplaceWith("Asset", "dev.kord.core.entity.Asset"), level = ERROR)
    public data class Avatar(val data: UserData, override val kord: Kord) : KordObject {

        /**
         * The default avatar url for this user. Discord uses this for users who don't have a custom avatar set.
         */
        val defaultUrl: String
            get() = "https://cdn.discordapp.com/embed/avatars/${
                when (@Suppress("DEPRECATION") val discriminator = data.discriminator.value) {
                    null, "0" -> (data.id.value shr 22) % 6u
                    else -> discriminator.toInt() % 5
                }
            }.png"

        /**
         * Whether the user has set their avatar.
         */
        val isCustom: Boolean get() = data.avatar != null

        /**
         * Whether the user has an animated avatar.
         */
        val isAnimated: Boolean get() = data.avatar?.startsWith("a_") ?: false

        /**
         * A supported format, prioritizing [Image.Format.GIF] for animated avatars and [Image.Format.PNG] for others.
         */
        val supportedFormat: Image.Format
            get() = when {
                isAnimated -> Image.Format.GIF
                else -> Image.Format.PNG
            }

        /**
         * Gets the avatar url in a supported format (defined by [supportedFormat]) and default size.
         */
        val url: String
            get() = getUrl(supportedFormat) ?: defaultUrl

        /**
         * Gets the avatar url in given [format], or returns null if the [format] is not supported.
         */
        public fun getUrl(format: Image.Format): String? {
            val hash = data.avatar ?: return defaultUrl
            if (!isAnimated && format == Image.Format.GIF) return null

            return "https://cdn.discordapp.com/avatars/${data.id.value}/$hash.${format.extension}"
        }

        /**
         * Gets the avatar url in a supported format and given [size].
         */
        public fun getUrl(size: Image.Size): String {
            return getUrl(supportedFormat, size)!!
        }

        /**
         * Gets the avatar url in given [format] and [size], or returns null if the [format] is not supported.
         */
        public fun getUrl(format: Image.Format, size: Image.Size): String? {
            val hash = data.avatar ?: return defaultUrl
            if (!isAnimated && format == Image.Format.GIF) return null

            return "https://cdn.discordapp.com/avatars/${data.id.value}/$hash.${format.extension}?size=${size.maxRes}"
        }

        /**
         * Requests to get the [defaultUrl] as an [Image].
         */
        public suspend fun getDefaultImage(): Image = Image.fromUrl(kord.resources.httpClient, defaultUrl)

        /**
         * Requests to get the avatar of the user as an [Image], prioritizing gif for animated avatars and png for others.
         */
        public suspend fun getImage(): Image = Image.fromUrl(kord.resources.httpClient, url)

        /**
         * Requests to get the avatar of the user as an [Image] given [format], or returns null if the format is not supported.
         */
        public suspend fun getImage(format: Image.Format): Image? {
            val url = getUrl(format) ?: return null

            return Image.fromUrl(kord.resources.httpClient, url)
        }

        /**
         * Requests to get the avatar of the user as an [Image] in given [size].
         */
        public suspend fun getImage(size: Image.Size): Image {
            return Image.fromUrl(kord.resources.httpClient, getUrl(size))
        }

        /**
         * Requests to get the avatar of the user as an [Image] given [format] and [size], or returns null if the
         * [format] is not supported.
         */
        public suspend fun getImage(format: Image.Format, size: Image.Size): Image? {
            val url = getUrl(format, size) ?: return null

            return Image.fromUrl(kord.resources.httpClient, url)
        }

        override fun toString(): String {
            return "Avatar(data=$data, kord=$kord)"
        }

    }

}
