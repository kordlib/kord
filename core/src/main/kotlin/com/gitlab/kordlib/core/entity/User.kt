package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.Image
import com.gitlab.kordlib.core.`object`.data.UserData
import com.gitlab.kordlib.core.behavior.UserBehavior

/**
 * An instance of a [Discord User](https://discordapp.com/developers/docs/resources/user#user-object).
 */
open class User(val data: UserData, override val kord: Kord) : UserBehavior {

    override val id: Snowflake
        get() = Snowflake(data.id)

    /**
     * The username of this user.
     */
    val username: String get() = data.username

    /**
     * The 4-digit code at the end of the user's discord tag.
     */
    val discriminator: String get() = data.discriminator

    /**
     * Whether or not this user is a bot.
     */
    val isBot: Boolean get() = data.bot

    /**
     * The complete user tag.
     */
    val tag: String get() = "$username#$discriminator"

    /**
     * The default avatar url for this user.
     */
    val defaultAvatarUrl: String get() = "https://cdn.discordapp.com/embed/avatars/${data.discriminator.toInt() % 5}.png"

    /**
     * Whether or not the user has an animated avatar.
     */
    val hasAnimatedAvatar: Boolean get() = data.avatar?.startsWith("a_") ?: false

    /**
     * Gets the avatar url in a supported format, prioritizing gif for animated avatars and png for others.
     */
    val avatarUrl: String
        get() = when {
            hasAnimatedAvatar -> getAvatarUrl(Image.Format.GIF)
            else -> getAvatarUrl(Image.Format.PNG)
        } ?: defaultAvatarUrl


    override suspend fun asUser(): User = this

    /**
     * Gets the avatar url in a supported format, or returns null if the format is not supported.
     */
    fun getAvatarUrl(format: Image.Format): String? {
        val hash = data.avatar ?: return defaultAvatarUrl
        if (!hasAnimatedAvatar && format == Image.Format.GIF) return null
        if (hasAnimatedAvatar && (format == Image.Format.PNG || format == Image.Format.JPEG)) return null

        return "https://cdn.discordapp.com/avatars/${id.value}/$hash.${format.extension}"
    }

    /**
     * Requests to get the default avatar as an image.
     */
    suspend fun getDefaultAvatar(): Image = TODO()

    /**
     * Requests to get the avatar as an image, or returns null if the format is not supported.
     *
     * @param format the requested image format, defaults to the behavior of [avatarUrl] if null.
     */
    suspend fun getAvatar(format: Image.Format? = null): Image? = TODO()

    override fun equals(other: Any?): Boolean {
        if (other !is UserBehavior) return false
        return id == other.id
    }

}