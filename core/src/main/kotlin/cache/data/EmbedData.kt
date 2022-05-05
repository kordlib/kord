@file:Suppress("DEPRECATION")

package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordEmbed
import dev.kord.common.entity.EmbedType
import dev.kord.common.entity.optional.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class EmbedData(
    val title: Optional<String> = Optional.Missing(),
    @Suppress("DEPRECATION")
    val type: Optional<EmbedType> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    val url: Optional<String> = Optional.Missing(),
    val timestamp: Optional<Instant> = Optional.Missing(),
    val color: OptionalInt = OptionalInt.Missing,
    val footer: Optional<EmbedFooterData> = Optional.Missing(),
    val image: Optional<EmbedImageData> = Optional.Missing(),
    val thumbnail: Optional<EmbedThumbnailData> = Optional.Missing(),
    val video: Optional<EmbedVideoData> = Optional.Missing(),
    val provider: Optional<EmbedProviderData> = Optional.Missing(),
    val author: Optional<EmbedAuthorData> = Optional.Missing(),
    val fields: Optional<List<EmbedFieldData>> = Optional.Missing(),
) {
    public companion object {
        public fun from(entity: DiscordEmbed): EmbedData = with(entity) {
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
public data class EmbedFooterData(
    val text: String,
    val iconUrl: Optional<String> = Optional.Missing(),
    val proxyIconUrl: Optional<String> = Optional.Missing(),
) {
    public companion object {
        public fun from(entity: DiscordEmbed.Footer): EmbedFooterData = with(entity) {
            EmbedFooterData(text, iconUrl, proxyIconUrl)
        }
    }
}

@Serializable
public data class EmbedImageData(
    val url: Optional<String> = Optional.Missing(),
    val proxyUrl: Optional<String> = Optional.Missing(),
    val height: OptionalInt = OptionalInt.Missing,
    val width: OptionalInt = OptionalInt.Missing,
) {
    public companion object {
        public fun from(entity: DiscordEmbed.Image): EmbedImageData = with(entity) {
            EmbedImageData(url, proxyUrl, height, width)
        }
    }
}

@Serializable
public data class EmbedThumbnailData(
    val url: Optional<String> = Optional.Missing(),
    val proxyUrl: Optional<String> = Optional.Missing(),
    val height: OptionalInt = OptionalInt.Missing,
    val width: OptionalInt = OptionalInt.Missing,
) {
    public companion object {
        public fun from(entity: DiscordEmbed.Thumbnail): EmbedThumbnailData = with(entity) {
            EmbedThumbnailData(url, proxyUrl, height, width)
        }
    }
}

@Serializable
public data class EmbedVideoData(
    val url: Optional<String> = Optional.Missing(),
    val height: OptionalInt = OptionalInt.Missing,
    val width: OptionalInt = OptionalInt.Missing,
) {
    public companion object {
        public fun from(entity: DiscordEmbed.Video): EmbedVideoData = with(entity) {
            EmbedVideoData(url, height, width)
        }
    }
}

@Serializable
public data class EmbedProviderData(
    val name: Optional<String> = Optional.Missing(),
    val url: Optional<String?> = Optional.Missing(), //see https://github.com/kordlib/kord/issues/149
) {
    public companion object {
        public fun from(entity: DiscordEmbed.Provider): EmbedProviderData = with(entity) {
            EmbedProviderData(name, url)
        }
    }
}

@Serializable
public data class EmbedAuthorData(
    val name: Optional<String> = Optional.Missing(),
    val url: Optional<String> = Optional.Missing(),
    val iconUrl: Optional<String> = Optional.Missing(),
    val proxyIconUrl: Optional<String> = Optional.Missing(),
) {
    public companion object {
        public fun from(entity: DiscordEmbed.Author): EmbedAuthorData = with(entity) {
            EmbedAuthorData(name, url, iconUrl, proxyIconUrl)
        }
    }
}

@Serializable
public data class EmbedFieldData(
    val name: String,
    val value: String,
    val inline: OptionalBoolean = OptionalBoolean.Missing,
) {
    public companion object {
        public fun from(entity: DiscordEmbed.Field): EmbedFieldData = with(entity) {
            EmbedFieldData(name, value, inline)
        }
    }
}
