package dev.kord.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Representation of a locale [supported by Discord](https://discord.com/developers/docs/reference#locales).
 *
 * @property language A language code representing the language.
 * @property country A country code representing the country.
 */
@Serializable(with = Locale.NewSerializer::class)
public data class Locale(val language: String, val country: String? = null) {
    public companion object {

        /**
         * Indonesian.
         */
        public val INDONESIAN: Locale = Locale("id")

        /**
         * Danish.
         */
        public val DANISH: Locale = Locale("da")

        /**
         * German.
         */
        public val GERMAN: Locale = Locale("de")

        /**
         * English (Great Britain).
         */
        public val ENGLISH_GREAT_BRITAIN: Locale = Locale("en", "GB")

        /**
         * English (United States).
         */
        public val ENGLISH_UNITED_STATES: Locale = Locale("en", "US")

        /**
         * Spanish (Spain).
         */
        public val SPANISH_SPAIN: Locale = Locale("es", "ES")

        /**
         * Spanish (Latin America).
         */
        public val SPANISH_LATIN_AMERICA: Locale = Locale("es", "419")

        /**
         * French.
         */
        public val FRENCH: Locale = Locale("fr")

        /**
         * Croatian.
         */
        public val CROATIAN: Locale = Locale("hr")

        /**
         * Italian.
         */
        public val ITALIAN: Locale = Locale("it")

        /**
         * Lithuanian.
         */
        public val LITHUANIAN: Locale = Locale("lt")

        /**
         * Hungarian.
         */
        public val HUNGARIAN: Locale = Locale("hu")

        /**
         * Dutch.
         */
        public val DUTCH: Locale = Locale("nl")

        /**
         * Norwegian.
         */
        public val NORWEGIAN: Locale = Locale("no")

        /**
         * Polish.
         */
        public val POLISH: Locale = Locale("pl")

        /**
         * Portuguese (Brazil).
         */
        public val PORTUGUESE_BRAZIL: Locale = Locale("pt", "BR")

        /**
         * Romanian.
         */
        public val ROMANIAN: Locale = Locale("ro")

        /**
         * Finnish.
         */
        public val FINNISH: Locale = Locale("fi")

        /**
         * Swedish.
         */
        public val SWEDISH: Locale = Locale("sv", "SE")

        /**
         * Vietnamese.
         */
        public val VIETNAMESE: Locale = Locale("vi")

        /**
         * Turkish.
         */
        public val TURKISH: Locale = Locale("tr")

        /**
         * Czech.
         */
        public val CZECH: Locale = Locale("cs")

        /**
         * Greek.
         */
        public val GREEK: Locale = Locale("el")

        /**
         * Bulgarian.
         */
        public val BULGARIAN: Locale = Locale("bg")

        /**
         * Russian.
         */
        public val RUSSIAN: Locale = Locale("ru")

        /**
         * Ukrainian.
         */
        public val UKRAINIAN: Locale = Locale("uk")

        /**
         * Hindi.
         */
        public val HINDI: Locale = Locale("hi")

        /**
         * Thai.
         */
        public val THAI: Locale = Locale("th")

        /**
         * Chinese (China).
         */
        public val CHINESE_CHINA: Locale = Locale("zh", "CN")

        /**
         * Japanese.
         */
        public val JAPANESE: Locale = Locale("ja")

        /**
         * Chinese (Taiwan).
         */
        public val CHINESE_TAIWAN: Locale = Locale("zh", "TW")

        /**
         * Korean.
         */
        public val KOREAN: Locale = Locale("ko")


        /**
         * All languages [supported by Discord](https://discord.com/developers/docs/reference#locales).
         */
        public val ALL: List<Locale> = listOf(
            INDONESIAN,
            DANISH,
            GERMAN,
            ENGLISH_GREAT_BRITAIN,
            ENGLISH_UNITED_STATES,
            SPANISH_SPAIN,
            SPANISH_LATIN_AMERICA,
            FRENCH,
            CROATIAN,
            ITALIAN,
            LITHUANIAN,
            HUNGARIAN,
            DUTCH,
            NORWEGIAN,
            POLISH,
            PORTUGUESE_BRAZIL,
            ROMANIAN,
            FINNISH,
            SWEDISH,
            VIETNAMESE,
            TURKISH,
            CZECH,
            GREEK,
            BULGARIAN,
            RUSSIAN,
            UKRAINIAN,
            HINDI,
            THAI,
            CHINESE_CHINA,
            JAPANESE,
            CHINESE_TAIWAN,
            KOREAN,
        )

        // https://regex101.com/r/8iMEWT/1
        private val languageTagFormat = "([a-z]{2})(?:-([A-Z]{2}|\\d{3}))?".toRegex()

        /**
         * Decodes the [Locale] from a `languageCode-countryCode` or `languageCode` format.
         *
         * This does not validate the actual [language] and [country], it just validates the format.
         *
         * @throws IllegalArgumentException if [string] is not a valid [Locale].
         */
        public fun fromString(string: String): Locale {
            val match = requireNotNull(languageTagFormat.matchEntire(string)) { "$string is not a valid Locale" }
            val (language, country) = match.destructured

            return ALL.firstOrNull { (l, c) ->
                language == l && country == (c ?: "")
            } ?: Locale(language, country.ifBlank { null })
        }
    }

    @Deprecated(
        "Replaced by 'Locale.serializer()'.",
        ReplaceWith("Locale.serializer()", imports = ["dev.kord.common.Locale"]),
        DeprecationLevel.HIDDEN,
    )
    public object Serializer : KSerializer<Locale> by NewSerializer

    // TODO rename to 'Serializer' once deprecated public serializer is removed
    internal object NewSerializer : KSerializer<Locale> {
        override val descriptor = PrimitiveSerialDescriptor("dev.kord.common.Locale", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Locale) =
            encoder.encodeString("${value.language}${value.country?.let { "-$it" } ?: ""}")

        override fun deserialize(decoder: Decoder) = fromString(decoder.decodeString())
    }
}
