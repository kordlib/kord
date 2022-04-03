@file:Suppress("DEPRECATION")

package dev.kord.core.entity

import dev.kord.common.Color
import dev.kord.common.entity.EmbedType
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.*
import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.datetime.Instant

internal const val embedDeprecationMessage = """
Embed types should be considered deprecated and might be removed in a future API version.

https://discord.com/developers/docs/resources/channel#embed-object-embed-types
"""

/**
 * An instance of a [Discord Embed](https://discord.com/developers/docs/resources/channel#embed-object).
 */
public data class Embed(val data: EmbedData, override val kord: Kord) : KordObject {

    /**
     * The title, if present.
     */
    public val title: String? get() = data.title.value

    /*
     * The type, [Embed.Type.Rich] for webhook and bot created embeds. Null if unknown.
     */
    @Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
    @Deprecated(embedDeprecationMessage)
    val type: EmbedType?
        get() = data.type.value

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
    val timestamp: Instant? get() = data.timestamp.value

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

    public data class Footer(val data: EmbedFooterData, override val kord: Kord) : KordObject {
        val text: String get() = data.text
        val iconUrl: String? get() = data.iconUrl.value
        val proxyIconUrl: String? get() = data.proxyIconUrl.value
    }

    public data class Image(val data: EmbedImageData, override val kord: Kord) : KordObject {
        val url: String? get() = data.url.value
        val proxyUrl: String? get() = data.proxyUrl.value
        val height: Int? get() = data.height.value
        val width: Int? get() = data.width.value
    }

    public data class Thumbnail(val data: EmbedThumbnailData, override val kord: Kord) : KordObject {
        val url: String? get() = data.url.value
        val proxyUrl: String? get() = data.proxyUrl.value
        val height: Int? get() = data.height.value
        val width: Int? get() = data.width.value
    }

    public data class Video(val data: EmbedVideoData, override val kord: Kord) : KordObject {
        val url: String? get() = data.url.value
        val height: Int? get() = data.height.value
        val width: Int? get() = data.width.value
    }

    public data class Provider(val data: EmbedProviderData, override val kord: Kord) : KordObject {
        val name: String? get() = data.name.value
        val url: String? get() = data.url.value
    }

    public data class Author(val data: EmbedAuthorData, override val kord: Kord) : KordObject {
        val name: String? get() = data.name.value
        val url: String? get() = data.url.value
        val iconUrl: String? get() = data.iconUrl.value
        val proxyIconUrl: String? get() = data.proxyIconUrl.value
    }

    public data class Field(val data: EmbedFieldData, override val kord: Kord) : KordObject {
        val name: String get() = data.name
        val value: String get() = data.value
        val inline: Boolean? get() = data.inline.value
    }

    /**
     * Applies this embed to the [builder], copying its properties to it.
     *
     * Properties that are part of this embed but not present in the [builder] will be ignored.
     */
    public fun apply(builder: EmbedBuilder) {
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
