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

/**
 * An instance of a [Discord Embed](https://discord.com/developers/docs/resources/channel#embed-object).
 *
 * @param data The [EmbedData] for the embed
 */
public data class Embed(val data: EmbedData, override val kord: Kord) : KordObject {

    /** The title, if present. */
    public val title: String? get() = data.title.value

    /** The type of embed, if present. Always [Rich][EmbedType.Rich] for webhook embeds. */
    val type: EmbedType? get() = data.type.value

    /** The description, if present. */
    val description: String? get() = data.description.value

    /** The title url, if present. */
    val url: String? get() = data.url.value

    /** The timestamp, if present. Unrelated to the creation time of the embed. */
    val timestamp: Instant? get() = data.timestamp.value

    /** The color of the embed, if present. */
    val color: Color? get() = data.color.value?.let { Color(it) }

    /** The footer, if present. */
    val footer: Footer? get() = data.footer.value?.let { Footer(it, kord) }

    /** The image, if present. */
    val image: Image? get() = data.image.value?.let { Image(it, kord) }

    /** The thumbnail, if present. */
    val thumbnail: Thumbnail? get() = data.thumbnail.value?.let { Thumbnail(it, kord) }

    /** The embedded video, if present. */
    val video: Video? get() = data.video.value?.let { Video(it, kord) }

    /** The embed provider, if present. */
    val provider: Provider? get() = data.provider.value?.let { Provider(it, kord) }

    /** The embed author, if present. */
    val author: Author? get() = data.author.value?.let { Author(it, kord) }

    /** The embed fields. */
    val fields: List<Field> get() = data.fields.orEmpty().map { Field(it, kord) }

    /**
     * Represents an Embed Footer.
     *
     * @param data The [EmbedFooterData] for the footer
     */
    public data class Footer(val data: EmbedFooterData, override val kord: Kord) : KordObject {
        /** The text for the footer. */
        val text: String get() = data.text
        /** The Icon URL for the footer. */
        val iconUrl: String? get() = data.iconUrl.value
        /** The proxy Icon URL for the footer. */
        val proxyIconUrl: String? get() = data.proxyIconUrl.value
    }

    /**
     * Represents an Image in an embed.
     *
     * @param data the [EmbedImageData] for the image.
     */
    public data class Image(val data: EmbedImageData, override val kord: Kord) : KordObject {
        /** The URL for the image. */
        val url: String? get() = data.url.value
        /** The proxy URL for the image. */
        val proxyUrl: String? get() = data.proxyUrl.value
        /** The height of the image. */
        val height: Int? get() = data.height.value
        /** The width of the image. */
        val width: Int? get() = data.width.value
    }

    /**
     * Represents a thumbnail in an embed.
     *
     * @param data the [EmbedThumbnailData] for the thumbnail.
     */
    public data class Thumbnail(val data: EmbedThumbnailData, override val kord: Kord) : KordObject {
        /** The URL for the thumbnail. */
        val url: String? get() = data.url.value
        /** The proxy URL for the thumbnail. */
        val proxyUrl: String? get() = data.proxyUrl.value
        /** The height of the thumbnail. */
        val height: Int? get() = data.height.value
        /** The width of the thumbnail. */
        val width: Int? get() = data.width.value
    }

    /**
     * Represents a video in an embed.
     *
     * @param data the [EmbedVideoData] for the video.
     */
    public data class Video(val data: EmbedVideoData, override val kord: Kord) : KordObject {
        /** The URL For the video*/
        val url: String? get() = data.url.value
        /** The height of the video. */
        val height: Int? get() = data.height.value
        /** The width of the video. */
        val width: Int? get() = data.width.value
    }

    /**
     * Represents a provider in an embed.
     *
     * @param data the [EmbedProviderData] for the provider.
     */
    public data class Provider(val data: EmbedProviderData, override val kord: Kord) : KordObject {
        /** The name of the provider. */
        val name: String? get() = data.name.value
        /** The URL for the provider. */
        val url: String? get() = data.url.value
    }

    /**
     * Represents an author in an embed.
     *
     * @param data the [EmbedAuthorData] for the author.
     */
    public data class Author(val data: EmbedAuthorData, override val kord: Kord) : KordObject {
        /** The name of the author. */
        val name: String? get() = data.name.value
        /** The URL for the author. */
        val url: String? get() = data.url.value
        /** The URL for the icon. */
        val iconUrl: String? get() = data.iconUrl.value
        /** The proxy URL for the icon. */
        val proxyIconUrl: String? get() = data.proxyIconUrl.value
    }

    /**
     * Represents a field in an embed.
     *
     * @param data the [EmbedFieldData] for the field.
     */
    public data class Field(val data: EmbedFieldData, override val kord: Kord) : KordObject {
        /** The name of the field. */
        val name: String get() = data.name
        /** The value of the field. */
        val value: String get() = data.value
        /** Whether to inline the field or not. */
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
