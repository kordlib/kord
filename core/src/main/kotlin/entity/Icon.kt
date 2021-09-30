package dev.kord.core.entity

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.EmojiData
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.UserData
import dev.kord.rest.Image

sealed class Icon(override val kord: Kord, val animated: Boolean, private val rawAssetUri: String) : KordObject {

    val format: Image.Format
        get() = when {
            animated -> Image.Format.GIF
            else -> Image.Format.PNG
        }

    val url: String
        get() = "$rawAssetUri.${format.extension}"

    fun getUrl(format: Image.Format): String? {
        if (format == Image.Format.GIF && !animated) return null
        return "$rawAssetUri.${format.extension}"
    }

    fun getUrl(size: Image.Size): String {
        return "$url?size=${size.maxRes}"
    }

    fun getUrl(format: Image.Format, size: Image.Size): String? {
        if (format == Image.Format.GIF && !animated) return null
        return "$rawAssetUri.${format.extension}?size=${size.maxRes}"
    }

    suspend fun getImage(): Image = Image.fromUrl(kord.resources.httpClient, url)

    suspend fun getImage(size: Image.Size): Image = Image.fromUrl(kord.resources.httpClient, getUrl(size))

    suspend fun getImage(format: Image.Format): Image? =
        getUrl(format)?.let { Image.fromUrl(kord.resources.httpClient, it) }

    suspend fun getImage(format: Image.Format, size: Image.Size): Image? =
        getUrl(format, size)?.let { Image.fromUrl(kord.resources.httpClient, it) }

    override fun toString(): String {
        return "Icon(type=${javaClass.name},animated=$animated,rawAssetUri=$rawAssetUri,kord=$kord)"
    }

    class EmojiIcon(kord: Kord, data: EmojiData) : Icon(
        kord,
        data.animated.discordBoolean,
        "$CDN_BASE_URL/emojis/${data.id.asString}"
    )

    class DefaultUserAvatar(kord: Kord, data: UserData) :
        Icon(kord, false, "$CDN_BASE_URL/embed/avatars/${data.discriminator.toInt() % 5}")

    class UserAvatar(kord: Kord, data: UserData) :
        Icon(
            kord,
            data.avatar!!.startsWith("a_"),
            "$CDN_BASE_URL/avatars/${data.id.asString}/${data.avatar}"
        )

    class MemberAvatar(kord: Kord, data: MemberData) :
        Icon(
            kord,
            data.avatar.value!!.startsWith("a_"),
            "$CDN_BASE_URL/guilds/${data.guildId.asString}/users/${data.userId.asString}/avatars/${data.avatar.value!!}"
        )

    companion object {
        private const val CDN_BASE_URL = "https://cdn.discordapp.com"
    }
}