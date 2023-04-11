package dev.kord.rest.builder.message

import dev.kord.common.Color
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.*
import kotlinx.datetime.Instant

/**
 * A builder for discord embeds.
 *
 * Inline Markdown links are supported in in all description-like fields.
 */
@KordDsl
@Suppress("unused")
public class EmbedBuilder : RequestBuilder<EmbedRequest> {
    public companion object {
        /**
         * Utility default value for fields.
         * While blank values are not allowed, this value can be used to trick Discord into rendering a Field as empty.
         */
        public const val ZERO_WIDTH_SPACE: String = "\u200E"
    }

    private var _title: Optional<String> = Optional.Missing()

    /**
     * The title of the embed. Limited to the length of [Limits.title].
     */
    public var title: String? by ::_title.delegate()

    private var _description: Optional<String> = Optional.Missing()

    /**
     * The description of the embed. Limited to the length of [Limits.description].
     */
    public var description: String? by ::_description.delegate()


    private var _url: Optional<String> = Optional.Missing()

    /**
     * The url of the embed's [title].
     */
    public var url: String? by ::_url.delegate()

    private var _timestamp: Optional<Instant> = Optional.Missing()

    /**
     * The timestamp displayed at the bottom of the embed.
     */
    public var timestamp: Instant? by ::_timestamp.delegate()

    private var _color: Optional<Color> = Optional.Missing()

    /**
     * The color of the embed.
     */
    public var color: Color? by ::_color.delegate()


    private var _image: Optional<String> = Optional.Missing()

    /**
     * The image url of the embed.
     */
    public var image: String? by ::_image.delegate()

    private var _footer: Optional<Footer> = Optional.Missing()

    /**
     * The footer of the embed.
     */
    public var footer: Footer? by ::_footer.delegate()

    private var _thumbnail: Optional<Thumbnail> = Optional.Missing()

    /**
     * The thumbnail of the embed.
     */
    public var thumbnail: Thumbnail? by ::_thumbnail.delegate()

    private var _author: Optional<Author> = Optional.Missing()

    /**
     * The author of the embed.
     */
    public var author: Author? by ::_author.delegate()

    /**
     * The embed fields.
     */
    public var fields: MutableList<Field> = mutableListOf()

    /**
     * Adds or updates the [footer] as configured by the [builder].
     */
    public inline fun footer(builder: Footer.() -> Unit) {
        footer = (footer ?: Footer()).apply(builder)
    }

    /**
     * Adds or updates the [thumbnail] as configured by the [builder].
     */
    public inline fun thumbnail(builder: Thumbnail.() -> Unit) {
        thumbnail = (thumbnail ?: Thumbnail()).apply(builder)
    }

    /**
     * Adds or updates the [author] as configured by the [builder].
     */
    public inline fun author(builder: Author.() -> Unit) {
        author = (author ?: Author()).apply(builder)
    }

    /**
     * Adds a new [Field] configured by the [builder].
     */
    public inline fun field(builder: Field.() -> Unit) {
        fields.add(Field().apply(builder))
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
    public inline fun field(name: String, inline: Boolean = false, value: () -> String = { ZERO_WIDTH_SPACE }) {
        val field = Field()
        field.name = name
        field.inline = inline
        field.value = value()

        fields.add(field)
    }

    override fun toRequest(): EmbedRequest = EmbedRequest(
        _title,
        Optional.Value("embed"),
        _description,
        _url,
        _timestamp,
        _color,
        _footer.map { it.toRequest() },
        _image.map { EmbedImageRequest(it) },
        _thumbnail.map { it.toRequest() },
        _author.map { it.toRequest() },
        Optional.missingOnEmpty(fields).mapList { it.toRequest() }
    )

    @KordDsl
    public class Thumbnail : RequestBuilder<EmbedThumbnailRequest> {

        /**
         * The image url of the thumbnail. This field is required.
         */
        public lateinit var url: String

        override fun toRequest(): EmbedThumbnailRequest = EmbedThumbnailRequest(url)
    }

    @KordDsl
    public class Footer : RequestBuilder<EmbedFooterRequest> {

        /**
         * The text of the footer. This field is required and limited to the length of [Limits.text].
         */
        public lateinit var text: String

        /**
         * The icon url to display.
         */
        public var icon: String? = null

        override fun toRequest(): EmbedFooterRequest = EmbedFooterRequest(text, icon)

        public object Limits {
            public const val text: Int = 2048
        }
    }

    @KordDsl
    public class Author : RequestBuilder<EmbedAuthorRequest> {

        private var _name: Optional<String> = Optional.Missing()

        /**
         * The name of the author. This field is required if [url] is not null.
         */
        public var name: String? by ::_name.delegate()

        private var _url: Optional<String> = Optional.Missing()

        /**
         * The link that will be applied to the author's [name]. [name] is a mandatory field if [url] is not null.
         */
        public var url: String? by ::_url.delegate()


        private var _icon: Optional<String> = Optional.Missing()

        /**
         * The image url that will be displayed next to the author's name.
         */
        public var icon: String? by ::_icon.delegate()

        override fun toRequest(): EmbedAuthorRequest = EmbedAuthorRequest(_name, _url, _icon)

        public object Limits {
            /**
             * The maximum length of the [Author.name] field.
             */
            public const val name: Int = 256
        }
    }

    @KordDsl
    public class Field : RequestBuilder<EmbedFieldRequest> {

        /**
         *  The value or 'description' of the [Field], [ZERO_WIDTH_SPACE] by default.
         *  Limited to the length of [Limits.value].
         *
         *  Blank values are not allowed, resulting an exception being thrown on the completion of the request.
         *  Use [ZERO_WIDTH_SPACE] instead to simulate an empty [value].
         */
        public var value: String = ZERO_WIDTH_SPACE

        /**
         * The name or 'title' of the [Field], [ZERO_WIDTH_SPACE] by default.
         * Limited in to the length of [Limits.name].
         *
         * Blank values are not allowed, resulting an exception being thrown on the completion of the request.
         * Use [ZERO_WIDTH_SPACE] instead to simulate an empty [value].
         */
        public var name: String = ZERO_WIDTH_SPACE


        private var _inline: OptionalBoolean = OptionalBoolean.Missing

        /**
         * Whether the field should be rendered inline, `false` by default.
         */
        public var inline: Boolean? by ::_inline.delegate()

        override fun toRequest(): EmbedFieldRequest = EmbedFieldRequest(name, value, _inline)

        public object Limits {
            /**
             * The maximum length of the [Field.name] field.
             */
            public const val name: Int = 256

            /**
             * The maximum length of the [Field.value] field.
             */
            public const val value: Int = 1024
        }
    }

    public object Limits {
        /**
         * The maximum length of the [EmbedBuilder.title] field.
         */
        public const val title: Int = 256

        /**
         * The maximum length of the [EmbedBuilder.description] field.
         */
        public const val description: Int = 4096

        /**
         * The maximum amount of [EmbedBuilder.Field] in an [EmbedBuilder].
         */
        public const val fieldCount: Int = 25

        /**
         * The maximum length of all text inside the [EmbedBuilder].
         */
        public const val total: Int = 6000
    }
}
