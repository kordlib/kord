package com.gitlab.kordlib.rest.builder.message

import com.gitlab.kordlib.common.Color
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.*
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * A builder for discord embeds.
 *
 * Inline Markdown links are supported in in all description-like fields.
 */
@KordDsl
@Suppress("unused")
class EmbedBuilder : RequestBuilder<EmbedRequest> {
    companion object {
        /**
         * Utility default value for fields.
         * While blank values are not allowed, this value can be used to trick Discord into rendering a Field as empty.
         */
        const val ZERO_WIDTH_SPACE = "\u200E"
    }

    /**
     * The title of the embed. Limited to the length of [Limits.title].
     */
    var title: String? = null

    /**
     * The description of the embed. Limited to the length of [Limits.description].
     */
    var description: String? = null

    /**
     * The url of the embed's [title].
     */
    var url: String? = null

    /**
     * The timestamp displayed at the bottom of the embed.
     */
    var timestamp: Instant? = null

    /**
     * The color of the embed.
     */
    var color: Color? = null

    /**
     * The image url of the embed.
     */
    var image: String? = null

    /**
     * The footer of the embed.
     */
    var footer: Footer? = null

    /**
     * The thumbnail of the embed.
     */
    var thumbnail: Thumbnail? = null

    /**
     * The author of the embed.
     */
    var author: Author? = null

    /**
     * The embed fields.
     */
    val fields: MutableList<Field> = mutableListOf()

    /**
     * Adds or updates the [footer] as configured by the [builder].
     */
    inline fun footer(builder: Footer.() -> Unit) {
        footer = (footer ?: Footer()).apply(builder)
    }

    /**
     * Adds or updates the [thumbnail] as configured by the [builder].
     */
    inline fun thumbnail(builder: Thumbnail.() -> Unit) {
        thumbnail = (thumbnail ?: Thumbnail()).apply(builder)
    }

    /**
     * Adds or updates the [author] as configured by the [builder].
     */
    inline fun author(builder: Author.() -> Unit) {
        author = (author ?: Author()).apply(builder)
    }

    /**
     * Adds a new [Field] configured by the [builder].
     */
    inline fun field(builder: Field.() -> Unit) {
        fields += Field().apply(builder)
    }

    /**
     * Adds a new [Field] using the given [name] and [value].
     *
     * @param inline Whether the field should be rendered inline, `false` by default.
     *
     * @param value The value or 'description' of the [Field], [ZERO_WIDTH_SPACE] by default.
     * Limited to the length of [Field.Limits.value].
     *
     * @param name The name or 'title' of the [Field], [ZERO_WIDTH_SPACE] by default.
     * Limited in to the length of [Field.Limits.name].
     *
     */
    inline fun field(name: String, inline: Boolean = false, value: () -> String = { ZERO_WIDTH_SPACE }) {
        val field = Field()
        field.name = name
        field.inline = inline
        field.value = value()

        fields += field
    }

    override fun toRequest(): EmbedRequest = EmbedRequest(
            title,
            "embed",
            description,
            url,
            timestamp?.let { DateTimeFormatter.ISO_INSTANT.format(it) },
            color?.rgb?.and(0xFFFFFF),
            footer?.toRequest(),
            image?.let(::EmbedImageRequest),
            thumbnail?.toRequest(),
            author?.toRequest(),
            fields.map { it.toRequest() }
    )

    @KordDsl
    class Thumbnail : RequestBuilder<EmbedThumbnailRequest> {

        /**
         * The image url of the thumbnail. This field is required.
         */
        lateinit var url: String

        override fun toRequest() = EmbedThumbnailRequest(url)
    }

    @KordDsl
    class Footer : RequestBuilder<EmbedFooterRequest> {

        /**
         * The text of the footer. This field is required and limited to the length of [Limits.text].
         */
        lateinit var text: String

        /**
         * The icon url to display.
         */
        var icon: String? = null

        override fun toRequest() = EmbedFooterRequest(text, icon)

        object Limits {
            const val text = 2048
        }
    }

    @KordDsl
    class Author : RequestBuilder<EmbedAuthorRequest> {

        /**
         * The name of the author. This field is required if [url] is not null.
         */
        var name: String? = null

        /**
         * The link that will be applied to the author's [name]. [name] is a mandatory field if [url] is not null.
         */
        var url: String? = null

        /**
         * The image url that will be displayed next to the author's name.
         */
        var icon: String? = null

        override fun toRequest() = EmbedAuthorRequest(name, url, icon)

        object Limits {
            /**
             * The maximum length of the [Author.name] field.
             */
            const val name = 256
        }
    }

    @KordDsl
    class Field : RequestBuilder<EmbedFieldRequest> {

        /**
         *  The value or 'description' of the [Field], [ZERO_WIDTH_SPACE] by default.
         *  Limited to the length of [Limits.value].
         *
         *  Blank values are not allowed, resulting an exception being thrown on the completion of the request.
         *  Use [ZERO_WIDTH_SPACE] instead to simulate an empty [value].
         */
        @Suppress("JoinDeclarationAndAssignment", "UNNECESSARY_LATEINIT")
        lateinit var value: String

        /**
         * The name or 'title' of the [Field], [ZERO_WIDTH_SPACE] by default.
         * Limited in to the length of [Limits.name].
         *
         * Blank values are not allowed, resulting an exception being thrown on the completion of the request.
         * Use [ZERO_WIDTH_SPACE] instead to simulate an empty [value].
         */
        @Suppress("JoinDeclarationAndAssignment", "UNNECESSARY_LATEINIT")
        lateinit var name: String

        /**
         * Whether the field should be rendered inline, `false` by default.
         */
        var inline: Boolean = false

        init {
            //these declarations and assignments are separated to maintain binary compatibility.
            //`lateinit` fields expose the JVM fields, and this was the cleanest solution I could think of
            // to maintain that field access.
            value = ZERO_WIDTH_SPACE
            name = ZERO_WIDTH_SPACE
        }


        override fun toRequest() = EmbedFieldRequest(name, value, inline)

        object Limits {
            /**
             * The maximum length of the [Field.name] field.
             */
            const val name = 256

            /**
             * The maximum length of the [Field.value] field.
             */
            const val value = 2048
        }
    }

    object Limits {
        /**
         * The maximum length of the [EmbedBuilder.title] field.
         */
        const val title = 256

        /**
         * The maximum length of the [EmbedBuilder.description] field.
         */
        const val description = 2048

        /**
         * The maximum amount of [EmbedBuilder.Field] in an [EmbedBuilder].
         */
        const val fieldCount = 25

        /**
         * The maximum length of all text inside the [EmbedBuilder].
         */
        const val total = 6000
    }
}
