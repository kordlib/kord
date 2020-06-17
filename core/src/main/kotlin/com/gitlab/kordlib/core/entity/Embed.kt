package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.Color
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.toInstant
import com.gitlab.kordlib.rest.builder.message.EmbedBuilder
import java.time.Instant

internal const val embedDeprecationMessage = """
Embed types should be considered deprecated and might be removed in a future API version.

https://discord.com/developers/docs/resources/channel#embed-object-embed-types
"""

/**
 * An instance of a [Discord Embed](https://discord.com/developers/docs/resources/channel#embed-object).
 */
data class Embed(val data: EmbedData, override val kord: Kord) : KordObject {

    /**
     * The title, if present.
     */
    val title: String? get() = data.title

    /*
     * The type, [Embed.Type.Rich] for webhook and bot created embeds. Null if unknown.
     */
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(embedDeprecationMessage)
    val type: Type? get() = Type.values().firstOrNull { it.value == data.type }

    /**
     * The description, if present.
     */
    val description: String? get() = data.description

    /**
     * The title url, if present.
     */
    val url: String? get() = data.url

    /**
     * The timestamp, if present. Unrelated to the creation time of the embed.
     */
    val timestamp: Instant? get() = data.timestamp?.toInstant()

    /**
     * The color of the embed, if present.
     */
    val color: Color? get() = data.color?.let { Color(it) }

    /**
     * The footer, if present.
     */
    val footer: Footer? get() = data.footer?.let { Footer(it, kord) }

    /**
     * The image, if present.
     */
    val image: Image? get() = data.image?.let { Image(it, kord) }

    /**
     * The thumbnail, if present.
     */
    val thumbnail: Thumbnail? get() = data.thumbnail?.let { Thumbnail(it, kord) }

    /**
     * The embedded video, if present.
     */
    val video: Video? get() = data.video?.let { Video(it, kord) }

    /**
     * The embed provider, if present.
     */
    val provider: Provider? get() = data.provider?.let { Provider(it, kord) }

    /**
     * The embed author, if present.
     */
    val author: Author? get() = data.author?.let { Author(it, kord) }

    /**
     * The embed fields.
     */
    val fields: List<Field> = emptyList()

    /**
     * The type of embeds, this is an non-exhaustive list.
     */
    @Deprecated(embedDeprecationMessage)
    enum class Type(val value: String) {
        Image("image"),
        Link("link"),
        Rich("rich"),
        Video("video"),
        @Suppress("SpellCheckingInspection")
        Gifv("gifv"),
        Article("article")
    }

    data class Footer(val data: EmbedFooterData, override val kord: Kord) : KordObject {
        val text: String get() = data.text
        val iconUrl: String? get() = data.iconUrl
        val proxyIconUrl: String? get() = data.proxyIconUrl
    }

    data class Image(val data: EmbedImageData, override val kord: Kord) : KordObject {
        val url: String? get() = data.url
        val proxyUrl: String? get() = data.proxyUrl
        val height: Int? get() = data.height
        val width: Int? get() = data.width
    }

    data class Thumbnail(val data: EmbedThumbnailData, override val kord: Kord) : KordObject {
        val url: String? get() = data.url
        val proxyUrl: String? get() = data.proxyUrl
        val height: Int? get() = data.height
        val width: Int? get() = data.width
    }

    data class Video(val data: EmbedVideoData, override val kord: Kord) : KordObject {
        val url: String? get() = data.url
        val height: Int? get() = data.height
        val width: Int? get() = data.width
    }

    data class Provider(val data: EmbedProviderData, override val kord: Kord) : KordObject {
        val name: String? get() = data.name
        val url: String? get() = data.url
    }

    data class Author(val data: EmbedAuthorData, override val kord: Kord) : KordObject {
        val name: String? get() = data.name
        val url: String? get() = data.url
        val iconUrl: String? get() = data.iconUrl
        val proxyIconUrl: String? get() = data.proxyIconUrl
    }

    data class Field(val data: EmbedFieldData, override val kord: Kord) : KordObject {
        val name: String get() = data.name
        val value: String get() = data.value
        val inline: Boolean? get() = data.inline
    }

    /**
     * Applies this embed to the [builder], copying its properties to it.
     *
     * Properties that are part of this embed but not present in the [builder] will be ignored.
     */
    fun apply(builder: EmbedBuilder) {
        builder.color = color

        author?.let { author ->
            builder.author {
                this.icon = author.iconUrl
                this.url = author.url
                this.name = author.name
            }
        }

        thumbnail?.let { thumbnail ->
            builder.thumbnail {
                this.url = thumbnail.url ?: ""
            }
        }

        builder.title = title

        builder.url = url

        builder.description = description

        fields.forEach { field ->
            builder.field {
                name = field.name
                value = field.value
                inline = field.inline ?: false
            }
        }

        builder.image = image?.url

        footer?.let { footer ->
            builder.footer {
                this.text = footer.text
                this.icon = footer.iconUrl
            }
        }

        builder.timestamp = timestamp

    }
}
