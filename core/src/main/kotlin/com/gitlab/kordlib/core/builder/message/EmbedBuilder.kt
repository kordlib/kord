package com.gitlab.kordlib.core.builder.message

import com.gitlab.kordlib.rest.json.request.*
import java.awt.Color
import java.time.Instant
import java.time.format.DateTimeFormatter

class EmbedBuilder(
        var title: String? = null,
        var description: String? = null,
        var url: String? = null,
        var timestamp: Instant? = null,
        var color: Color? = null,

        var image: String? = null,
        var footer: Footer? = null,
        var thumbnail: Thumbnail? = null,
        var author: Author? = null,
        val fields: MutableList<Field> = mutableListOf()
) {
    inline fun footer(builder: Footer.() -> Unit) {
        footer = (footer ?: Footer()).apply(builder)
    }

    inline fun thumbnail(builder: Thumbnail.() -> Unit) {
        thumbnail = (thumbnail ?: Thumbnail()).apply(builder)
    }

    inline fun author(builder: Author.() -> Unit) {
        author = (author ?: Author()).apply(builder)
    }

    inline fun field(name: String, inline: Boolean = false, value: () -> String) {
        fields += Field(name, value(), inline)
    }

    fun toRequest(): EmbedRequest = EmbedRequest(
            title,
            "embed",
            description,
            url,
            timestamp?.let { DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(it) },
            color?.rgb,
            footer?.toRequest(),
            image?.let(::EmbedImageRequest),
            thumbnail?.toRequest(),
            author?.toRequest(),
            fields.map { it.toRequest() }
    )

    class Thumbnail(var url: String? = null) {
        fun toRequest() = url?.let(::EmbedThumbnailRequest)
    }

    class Footer(
            var text: String? = null,
            var url: String? = null,
            var icon: String? = null
    ) {
        fun toRequest() = EmbedFooterRequest(text!!, url, icon)
    }

    class Author(
            var name: String? = null,
            var url: String? = null,
            var icon: String? = null
    ) {
        fun toRequest() = EmbedAuthorRequest(name, url, icon)
    }

    class Field(
            var name: String,
            var value: String,
            var inline: Boolean? = null
    ) {
        fun toRequest() = EmbedFieldRequest(name, value, inline ?: false)
    }
}