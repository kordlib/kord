package dev.kord.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Locale as JLocale

/**
 * Representation of a locale [supported by Discord](https://discord.com/developers/docs/reference#locales).
 *
 * @property language an ISO 639-1 language code representing the language
 * @property country an ISO 3166 country code representing the country
 */
@Serializable(with = Locale.Serializer::class)
public data class Locale(val language: String, val country: String? = null) {
    /**
     * Converts this into a [JLocale].
     */
    public fun asJavaLocale(): JLocale = JLocale(language, country ?: "")

    @Suppress("MemberVisibilityCanBePrivate")
    public companion object {

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
         * All languages [supported by Discord](https://discord.com/developers/docs/reference#Locales).
         */
        public val ALL: List<Locale> = listOf(
            DANISH,
            GERMAN,
            ENGLISH_GREAT_BRITAIN,
            ENGLISH_UNITED_STATES,
            SPANISH_SPAIN,
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

        // https://regex101.com/r/KCHTj8/1
        private val languageTagFormat = "([a-z]{2})(?:-([A-Z]{2}))?".toRegex()

        /**
         * Decodes the language from a `languageCode-countryCode` or `languageCode` format.
         *
         * This does not validate the actually languages and countries, it just validates the format.
         *
         * @throws IllegalArgumentException if [string] is not a valid format.
         */
        public fun fromString(string: String): Locale {
            val match = requireNotNull(languageTagFormat.matchEntire(string)) { "$string is not a valid Locale" }
            val (language, country) = match.destructured

            return ALL.firstOrNull { (l, c) ->
                language == l && country == (c ?: "")
            } ?: Locale(language, country.ifBlank { null })
        }
    }

    public object Serializer : KSerializer<Locale> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Locale", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Locale) {
            encoder.encodeString("${value.language}${value.country?.let { "-$it" } ?: ""}")
        }

        override fun deserialize(decoder: Decoder): Locale = fromString(decoder.decodeString())
    }
}

/**
 * Converts this into a [Locale].
 */
public val JLocale.kLocale: Locale
    get() = Locale(language, country.ifBlank { null })
