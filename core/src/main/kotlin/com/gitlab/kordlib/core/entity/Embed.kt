@file:Suppress("DEPRECATION")

package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.Color
import com.gitlab.kordlib.common.entity.EmbedType
import com.gitlab.kordlib.common.entity.optional.orEmpty
import com.gitlab.kordlib.common.entity.optional.value
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
    val title: String? get() = data.title.value

    /*
     * The type, [Embed.Type.Rich] for webhook and bot created embeds. Null if unknown.
     */
    @Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
    @Deprecated(embedDeprecationMessage)
    val type: EmbedType? get() = data.type.value

    /**
     * The description, if present.
     */
    val description: String? get() = data.description.value

    /**
     * The title url, if present.
     */
    val url: String? get() = data.url.value

    /**
     * The timestamp, if present. Unrelated to the creation time of the embed.
     */
    val timestamp: Instant? get() = data.timestamp.value?.toInstant()

    /**
     * The color of the embed, if present.
     */
    val color: Color? get() = data.color.value?.let { Color(it) }

    /**
     * The footer, if present.
     */
    val footer: Footer? get() = data.footer.value?.let { Footer(it, kord) }

    /**
     * The image, if present.
     */
    val image: Image? get() = data.image.value?.let { Image(it, kord) }

    /**
     * The thumbnail, if present.
     */
    val thumbnail: Thumbnail? get() = data.thumbnail.value?.let { Thumbnail(it, kord) }

    /**
     * The embedded video, if present.
     */
    val video: Video? get() = data.video.value?.let { Video(it, kord) }

    /**
     * The embed provider, if present.
     */
    val provider: Provider? get() = data.provider.value?.let { Provider(it, kord) }

    /**
     * The embed author, if present.
     */
    val author: Author? get() = data.author.value?.let { Author(it, kord) }

    /**
     * The embed fields.
     */
    val fields: List<Field> get() = data.fields.orEmpty().map { Field(it, kord) }

    data class Footer(val data: EmbedFooterData, override val kord: Kord) : KordObject {
        val text: String get() = data.text
        val iconUrl: String? get() = data.iconUrl.value
        val proxyIconUrl: String? get() = data.proxyIconUrl.value
    }

    data class Image(val data: EmbedImageData, override val kord: Kord) : KordObject {
        val url: String? get() = data.url.value
        val proxyUrl: String? get() = data.proxyUrl.value
        val height: Int? get() = data.height.value
        val width: Int? get() = data.width.value
    }

    data class Thumbnail(val data: EmbedThumbnailData, override val kord: Kord) : KordObject {
        val url: String? get() = data.url.value
        val proxyUrl: String? get() = data.proxyUrl.value
        val height: Int? get() = data.height.value
        val width: Int? get() = data.width.value
    }

    data class Video(val data: EmbedVideoData, override val kord: Kord) : KordObject {
        val url: String? get() = data.url.value
        val height: Int? get() = data.height.value
        val width: Int? get() = data.width.value
    }

    data class Provider(val data: EmbedProviderData, override val kord: Kord) : KordObject {
        val name: String? get() = data.name.value
        val url: String? get() = data.url.value
    }

    data class Author(val data: EmbedAuthorData, override val kord: Kord) : KordObject {
        val name: String? get() = data.name.value
        val url: String? get() = data.url.value
        val iconUrl: String? get() = data.iconUrl.value
        val proxyIconUrl: String? get() = data.proxyIconUrl.value
    }

    data class Field(val data: EmbedFieldData, override val kord: Kord) : KordObject {
        val name: String get() = data.name
        val value: String get() = data.value
        val inline: Boolean? get() = data.inline.value
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

    override fun toString(): String {
        return "Embed(data=$data, kord=$kord, fields=$fields)"
    }
}
