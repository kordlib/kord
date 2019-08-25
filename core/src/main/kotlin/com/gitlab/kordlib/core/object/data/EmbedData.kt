package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.common.entity.Embed
import kotlinx.serialization.Serializable

@Serializable
data class EmbedData(
        val title: String? = null,
        val type: String? = null,
        val description: String? = null,
        val url: String? = null,
        val timestamp: String? = null,
        val color: Int? = null,
        val footer: EmbedFooterData? = null,
        val image: EmbedImageData? = null,
        val thumbnail: EmbedThumbnailData? = null,
        val video: EmbedVideoData? = null,
        val provider: EmbedProviderData? = null,
        val author: EmbedAuthorData? = null,
        val fields: List<EmbedFieldData> = emptyList()
) {
    companion object {
        fun from(entity: Embed) = with(entity) {
            EmbedData(
                    title,
                    type,
                    description,
                    url,
                    timestamp,
                    color,
                    footer?.let { EmbedFooterData.from(it) },
                    image?.let { EmbedImageData.from(it) },
                    thumbnail?.let { EmbedThumbnailData.from(it) },
                    video?.let { EmbedVideoData.from(it) },
                    provider?.let { EmbedProviderData.from(it) },
                    author?.let { EmbedAuthorData.from(it) },
                    fields.orEmpty().map { EmbedFieldData.from(it) }
            )
        }
    }
}

@Serializable
data class EmbedFooterData(
        val text: String,
        val iconUrl: String? = null,
        val proxyIconUrl: String? = null
) {
    companion object {
        fun from(entity: Embed.Footer) = with(entity) {
            EmbedFooterData(text, iconUrl, proxyIconUrl)
        }
    }
}

@Serializable
data class EmbedImageData(
        val url: String? = null,
        val proxyUrl: String? = null,
        val height: Int? = null,
        val width: Int? = null
) {
    companion object {
        fun from(entity: Embed.Image) = with(entity) {
            EmbedImageData(url, proxyUrl, height, width)
        }
    }
}

@Serializable
data class EmbedThumbnailData(
        val url: String? = null,
        val proxyUrl: String? = null,
        val height: Int? = null,
        val width: Int? = null
) {
    companion object {
        fun from(entity: Embed.Thumbnail) = with(entity) {
            EmbedThumbnailData(url, proxyUrl, height, width)
        }
    }
}

@Serializable
data class EmbedVideoData(
        val url: String? = null,
        val height: Int? = null,
        val width: Int? = null
) {
    companion object {
        fun from(entity: Embed.Video) = with(entity) {
            EmbedVideoData(url, height, width)
        }
    }
}

@Serializable
data class EmbedProviderData(
        val name: String? = null,
        val url: String? = null
) {
    companion object {
        fun from(entity: Embed.Provider) = with(entity) {
            EmbedProviderData(name, url)
        }
    }
}

@Serializable
data class EmbedAuthorData(
        val name: String? = null,
        val url: String? = null,
        val iconUrl: String? = null,
        val proxyIconUrl: String? = null
) {
    companion object {
        fun from(entity: Embed.Author) = with(entity) {
            EmbedAuthorData(name, url, iconUrl, proxyIconUrl)
        }
    }
}

@Serializable
data class EmbedFieldData(
        val name: String,
        val value: String,
        val inline: Boolean? = null
) {
    companion object {
        fun from(entity: Embed.Field) = with(entity) {
            EmbedFieldData(name, value, inline)
        }
    }
}