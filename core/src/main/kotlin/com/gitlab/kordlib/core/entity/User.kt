package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Premium
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.UserFlags
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.cache.data.UserData
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.rest.Image
import java.util.*

/**
 * An instance of a [Discord User](https://discord.com/developers/docs/resources/user#user-object).
 */
open class User(
        val data: UserData,
        override val kord: Kord, override val supplier: EntitySupplier = kord.defaultSupplier
) : UserBehavior {

    override val id: Snowflake
        get() = Snowflake(data.id)

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
    val flags: UserFlags? get() = data.flags

    /**
     * The type of Nitro subscription on a user's account, if present.
     */
    val premiumType: Premium? get() = data.premium

    /**
     * The complete user tag.
     */
    val tag: String get() = "$username#$discriminator"

    /**
     * Whether this user is a bot, if present.
     */
    val isBot: Boolean? get() = data.bot

    override suspend fun asUser(): User = this

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is UserBehavior -> other.id == id
        else -> false
    }

    /**
     * Returns a new [User] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): User = User(data, kord, strategy.supply(kord))

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
         * Gets the avatar url in a supported format, prioritizing [Image.Format.GIF] for animated avatars and [Image.Format.PNG] for others.
         */
        val url: String
            get() = when {
                isAnimated -> getUrl(Image.Format.GIF)
                else -> getUrl(Image.Format.PNG)
            } ?: defaultUrl

        /**
         * Gets the avatar url in a supported [Image.Format], or returns null if the [format] is not supported.
         */
        fun getUrl(format: Image.Format): String? {
            val hash = data.avatar ?: return defaultUrl
            if (!isAnimated && format == Image.Format.GIF) return null

            return "https://cdn.discordapp.com/avatars/${data.id}/$hash.${format.extension}"
        }

        /**
         * Requests to get the [defaultUrl] as an [Image].
         */
        suspend fun getDefaultImage(): Image = Image.fromUrl(kord.resources.httpClient, defaultUrl)

        /**
         * Requests to get the avatar of the user as an [Image], or returns null if the format is not supported.
         *
         * @param format the requested image format, defaults to the behavior of [url] if null.
         */
        suspend fun getImage(format: Image.Format): Image? {
            val url = getUrl(format) ?: return null

            return Image.fromUrl(kord.resources.httpClient, url)
        }

        /**
         * Requests to get the avatar of the user as an [Image], prioritizing gif for animated avatars and png for others.
         */
        suspend fun getImage(): Image = Image.fromUrl(kord.resources.httpClient, url)
    }

}