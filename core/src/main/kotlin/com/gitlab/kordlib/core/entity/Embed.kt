package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.`object`.data.*
import com.gitlab.kordlib.core.toInstant
import java.awt.Color
import java.time.Instant

data class Embed(val data: EmbedData, override val kord: Kord) : KordObject {

    val title: String? get() = data.title

    val type: Type? get() = Type.values().firstOrNull { it.value == data.type }

    val description: String? get() = data.description

    val url: String? get() = data.url

    val timestamp: Instant? get() = data.timestamp?.toInstant()

    val color: Color?get()  = data.color?.let { Color(it, true) }

    val footer: Footer? get() = data.footer?.let { Footer(it, kord) }
    val image: Image? get() = data.image?.let { Image(it, kord) }
    val thumbnail: Thumbnail? get() = data.thumbnail?.let { Thumbnail(it, kord) }
    val video: Video? get() = data.video?.let { Video(it, kord) }
    val provider: Provider? get() = data.provider?.let { Provider(it, kord) }
    val author: Author? get() = data.author?.let { Author(it, kord) }
    val fields: List<Field> = emptyList()

    enum class Type(val value: String) {
        Image("image"),
        Link("link"),
        Rich("rich"),
        Video("video")
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

    data class Video(val data: EmbedVideoData, override val kord: Kord): KordObject {
        val url: String? get() = data.url
        val height: Int? get() = data.height
        val width: Int? get() = data.width
    }

    data class Provider(val data: EmbedProviderData, override val kord: Kord): KordObject {
        val name: String? get() = data.name
        val url: String? get() = data.url
    }

    data class Author(val data: EmbedAuthorData, override val kord: Kord): KordObject {
        val name: String? get() = data.name
        val url: String? get() = data.url
        val iconUrl: String? get() = data.iconUrl
        val proxyIconUrl: String? get() = data.proxyIconUrl
    }

    data class Field(val data: EmbedFieldData, override val kord: Kord): KordObject {
        val name: String get() = data.name
        val value: String get() = data.value
        val inline: Boolean? get() = data.inline
    }
}