package com.gitlab.kordlib.rest.builder.message

import com.gitlab.kordlib.rest.builder.KordDsl
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.*
import java.awt.Color
import java.time.Instant
import java.time.format.DateTimeFormatter

@KordDsl
class EmbedBuilder : RequestBuilder<EmbedRequest> {
    var title: String? = null
    var description: String? = null
    var url: String? = null
    var timestamp: Instant? = null
    var color: Color? = null

    var image: String? = null
    var footer: Footer? = null
    var thumbnail: Thumbnail? = null
    var author: Author? = null
    val fields: MutableList<Field> = mutableListOf()

    inline fun footer(builder: Footer.() -> Unit) {
        footer = (footer ?: Footer()).apply(builder)
    }

    inline fun thumbnail(builder: Thumbnail.() -> Unit) {
        thumbnail = (thumbnail ?: Thumbnail()).apply(builder)
    }

    inline fun author(builder: Author.() -> Unit) {
        author = (author ?: Author()).apply(builder)
    }

    inline fun field(builder: Field.() -> Unit) {
        fields += Field().apply(builder)
    }

    override fun toRequest(): EmbedRequest = EmbedRequest(
            title,
            "embed",
            description,
            url,
            timestamp?.let { DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(it) },
            color?.rgb?.and(0xFFFFFF),
            footer?.toRequest(),
            image?.let(::EmbedImageRequest),
            thumbnail?.toRequest(),
            author?.toRequest(),
            fields.map { it.toRequest() }
    )

    @KordDsl
    class Thumbnail : RequestBuilder<EmbedThumbnailRequest> {
        lateinit var url: String

        override fun toRequest() = EmbedThumbnailRequest(url)
    }

    @KordDsl
    class Footer : RequestBuilder<EmbedFooterRequest> {
        lateinit var text: String
        var url: String? = null
        var icon: String? = null

        override fun toRequest() = EmbedFooterRequest(text, url, icon)
    }

    @KordDsl
    class Author : RequestBuilder<EmbedAuthorRequest> {
        var name: String? = null
        var url: String? = null
        var icon: String? = null

        override fun toRequest() = EmbedAuthorRequest(name, url, icon)
    }

    @KordDsl
    class Field: RequestBuilder<EmbedFieldRequest> {
        lateinit var value: String
        lateinit  var name: String
        var inline: Boolean = false

        override fun toRequest() = EmbedFieldRequest(name, value, inline)
    }
}
