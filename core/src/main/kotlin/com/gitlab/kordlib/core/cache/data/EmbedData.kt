@file:Suppress("DEPRECATION")

package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordEmbed
import com.gitlab.kordlib.common.entity.EmbedType
import com.gitlab.kordlib.common.entity.optional.*
import kotlinx.serialization.Serializable

@Serializable
data class EmbedData(
        val title: Optional<String> = Optional.Missing(),
        @Suppress("DEPRECATION")
        val type: Optional<EmbedType> = Optional.Missing(),
        val description: Optional<String> = Optional.Missing(),
        val url: Optional<String> = Optional.Missing(),
        val timestamp: Optional<String> = Optional.Missing(),
        val color: OptionalInt = OptionalInt.Missing,
        val footer: Optional<EmbedFooterData> = Optional.Missing(),
        val image: Optional<EmbedImageData> = Optional.Missing(),
        val thumbnail: Optional<EmbedThumbnailData> = Optional.Missing(),
        val video: Optional<EmbedVideoData> = Optional.Missing(),
        val provider: Optional<EmbedProviderData> = Optional.Missing(),
        val author: Optional<EmbedAuthorData> = Optional.Missing(),
        val fields: Optional<List<EmbedFieldData>> = Optional.Missing(),
) {
    companion object {
        fun from(entity: DiscordEmbed) = with(entity) {
            EmbedData(
                    title,
                    type,
                    description,
                    url,
                    timestamp,
                    color,
                    footer.map { EmbedFooterData.from(it) },
                    image.map { EmbedImageData.from(it) },
                    thumbnail.map { EmbedThumbnailData.from(it) },
                    video.map { EmbedVideoData.from(it) },
                    provider.map { EmbedProviderData.from(it) },
                    author.map { EmbedAuthorData.from(it) },
                    fields.mapList { EmbedFieldData.from(it) },
            )
        }
    }
}

@Serializable
data class EmbedFooterData(
        val text: String,
        val iconUrl: Optional<String> = Optional.Missing(),
        val proxyIconUrl: Optional<String> = Optional.Missing(),
) {
    companion object {
        fun from(entity: DiscordEmbed.Footer) = with(entity) {
            EmbedFooterData(text, iconUrl, proxyIconUrl)
        }
    }
}

@Serializable
data class EmbedImageData(
        val url: Optional<String> = Optional.Missing(),
        val proxyUrl: Optional<String> = Optional.Missing(),
        val height: OptionalInt = OptionalInt.Missing,
        val width: OptionalInt = OptionalInt.Missing,
) {
    companion object {
        fun from(entity: DiscordEmbed.Image) = with(entity) {
            EmbedImageData(url, proxyUrl, height, width)
        }
    }
}

@Serializable
data class EmbedThumbnailData(
        val url: Optional<String> = Optional.Missing(),
        val proxyUrl: Optional<String> = Optional.Missing(),
        val height: OptionalInt = OptionalInt.Missing,
        val width: OptionalInt = OptionalInt.Missing,
) {
    companion object {
        fun from(entity: DiscordEmbed.Thumbnail) = with(entity) {
            EmbedThumbnailData(url, proxyUrl, height, width)
        }
    }
}

@Serializable
data class EmbedVideoData(
        val url: Optional<String> = Optional.Missing(),
        val height: OptionalInt = OptionalInt.Missing,
        val width: OptionalInt = OptionalInt.Missing,
) {
    companion object {
        fun from(entity: DiscordEmbed.Video) = with(entity) {
            EmbedVideoData(url, height, width)
        }
    }
}

@Serializable
data class EmbedProviderData(
        val name: Optional<String> = Optional.Missing(),
        val url: Optional<String> = Optional.Missing(),
) {
    companion object {
        fun from(entity: DiscordEmbed.Provider) = with(entity) {
            EmbedProviderData(name, url)
        }
    }
}

@Serializable
data class EmbedAuthorData(
        val name: Optional<String> = Optional.Missing(),
        val url: Optional<String> = Optional.Missing(),
        val iconUrl: Optional<String> = Optional.Missing(),
        val proxyIconUrl: Optional<String> = Optional.Missing(),
) {
    companion object {
        fun from(entity: DiscordEmbed.Author) = with(entity) {
            EmbedAuthorData(name, url, iconUrl, proxyIconUrl)
        }
    }
}

@Serializable
data class EmbedFieldData(
        val name: String,
        val value: String,
        val inline: OptionalBoolean = OptionalBoolean.Missing,
) {
    companion object {
        fun from(entity: DiscordEmbed.Field) = with(entity) {
            EmbedFieldData(name, value, inline)
        }
    }
}