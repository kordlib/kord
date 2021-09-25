package dev.kord.core.entity

import dev.kord.common.Color
import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.UserFlags
import dev.kord.common.entity.UserPremium
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.UserData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.Image

/**
 * An instance of a [Discord User](https://discord.com/developers/docs/resources/user#user-object).
 */
open class User(
    val data: UserData,
    override val kord: Kord, override val supplier: EntitySupplier = kord.defaultSupplier,
) : UserBehavior {

    override val id: Snowflake
        get() = data.id

    val avatar: Avatar get() = Avatar(data, kord)

    /**
     * The username of this user.
     */
    val username: String get() = data.username

    /**
     * The 4-digit code at the end of the user's discord tag.
     */
    val discriminator: String get() = data.discriminator

    /**
     * The flags on a user's account, if present.
     */
    @Deprecated("Use publicFlags instead.", ReplaceWith("publicFlags"), DeprecationLevel.ERROR)
    @DeprecatedSinceKord("0.7.0")
    val flags: UserFlags? by ::publicFlags

    override suspend fun asUser(): User {
        return this
    }

    override suspend fun asUserOrNull(): User {
        return this
    }

    /**
     * The flags on a user's account, if present.
     */
    val publicFlags: UserFlags? get() = data.publicFlags.value

    /**
     * The type of Nitro subscription on a user's account, if present.
     */
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("premiumType is never present.", level = DeprecationLevel.ERROR)
    @DeprecatedSinceKord("0.7.0")
    val premiumType: UserPremium?
        get() = throw NotImplementedError("premiumType is no longer supported.")

    /**
     * The complete user tag.
     */
    val tag: String get() = "$username#$discriminator"

    /**
     * Whether this user is a bot account.
     */
    val isBot: Boolean get() = data.bot.discordBoolean

    val accentColor: Color? get() = data.accentColor?.let { Color(it) }

    fun getBannerUrl(format: Image.Format): String? =
        data.banner?.let { "https://cdn.discordapp.com/banners/${id.asString}/$it.${format.extension}" }


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

    data class Avatar(val data: UserData, override val kord: Kord) : KordObject {

        /**
         * The default avatar url for this user. Discord uses this for users who don't have a custom avatar set.
         */
        val defaultUrl: String get() = "https://cdn.discordapp.com/embed/avatars/${data.discriminator.toInt() % 5}.png"

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
        fun getUrl(format: Image.Format): String? {
            val hash = data.avatar ?: return defaultUrl
            if (!isAnimated && format == Image.Format.GIF) return null

            return "https://cdn.discordapp.com/avatars/${data.id.value}/$hash.${format.extension}"
        }

        /**
         * Gets the avatar url in a supported format and given [size].
         */
        fun getUrl(size: Image.Size): String {
            return getUrl(supportedFormat, size)!!
        }

        /**
         * Gets the avatar url in given [format] and [size], or returns null if the [format] is not supported.
         */
        fun getUrl(format: Image.Format, size: Image.Size): String? {
            val hash = data.avatar ?: return defaultUrl
            if (!isAnimated && format == Image.Format.GIF) return null

            return "https://cdn.discordapp.com/avatars/${data.id.value}/$hash.${format.extension}?size=${size.maxRes}"
        }

        /**
         * Requests to get the [defaultUrl] as an [Image].
         */
        suspend fun getDefaultImage(): Image = Image.fromUrl(kord.resources.httpClient, defaultUrl)

        /**
         * Requests to get the avatar of the user as an [Image], prioritizing gif for animated avatars and png for others.
         */
        suspend fun getImage(): Image = Image.fromUrl(kord.resources.httpClient, url)

        /**
         * Requests to get the avatar of the user as an [Image] given [format], or returns null if the format is not supported.
         */
        suspend fun getImage(format: Image.Format): Image? {
            val url = getUrl(format) ?: return null

            return Image.fromUrl(kord.resources.httpClient, url)
        }

        /**
         * Requests to get the avatar of the user as an [Image] in given [size].
         */
        suspend fun getImage(size: Image.Size): Image {
            return Image.fromUrl(kord.resources.httpClient, getUrl(size))
        }

        /**
         * Requests to get the avatar of the user as an [Image] given [format] and [size], or returns null if the
         * [format] is not supported.
         */
        suspend fun getImage(format: Image.Format, size: Image.Size): Image? {
            val url = getUrl(format, size) ?: return null

            return Image.fromUrl(kord.resources.httpClient, url)
        }

        override fun toString(): String {
            return "Avatar(data=$data, kord=$kord)"
        }

    }

}
