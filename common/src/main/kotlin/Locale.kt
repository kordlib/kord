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
 * Representation of a locale [supported by Discord](https://discord.com/developers/docs/reference#Locales).
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
         * English (United States).
         */
        public val ENGLISH_UNITED_STATES: Locale = Locale("en", "US")

        /**
         * English (Great Britain).
         */
        public val ENGLISH_GREAT_BRITAN: Locale = Locale("en", "GB")

        /**
         * Bulgarian.
         */
        public val BULGARIAN: Locale = Locale("bg")

        /**
         * Chinese (China).
         */
        public val CHINESE_CHINA: Locale = Locale("zh", "CN")

        /**
         * Chinese (Taiwan).
         */
        public val CHINESE_TAIWAN: Locale = Locale("zh", "TW")

        /**
         * Croatian.
         */
        public val CROATIAN: Locale = Locale("hr")

        /**
         * Czech.
         */
        public val CZECH: Locale = Locale("cs")

        /**
         * Danish.
         */
        public val DANISH: Locale = Locale("da")

        /**
         * Dutch.
         */
        public val DUTCH: Locale = Locale("nl")

        /**
         * Finnish.
         */
        public val FINNISH: Locale = Locale("fi")

        /**
         * French.
         */
        public val FRENCH: Locale = Locale("fr")

        /**
         * German.
         */
        public val GERMAN: Locale = Locale("de")

        /**
         * Greek.
         */
        public val GREEK: Locale = Locale("el")

        /**
         * Hindi.
         */
        public val HINDI: Locale = Locale("hi")

        /**
         * Hungarian.
         */
        public val HUNGARIAN: Locale = Locale("hu")

        /**
         * Italian.
         */
        public val ITALIAN: Locale = Locale("it")

        /**
         * Japanese.
         */
        public val JAPENESE: Locale = Locale("ja")

        /**
         * Korean.
         */
        public val KOREAN: Locale = Locale("ko")

        /**
         * Lithuanian.
         */
        public val LITHUANIAN: Locale = Locale("lt")

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
         * Russian.
         */
        public val RUSSIAN: Locale = Locale("ru")

        /**
         * Spanish (Spain).
         */
        public val SPANISH_SPAIN: Locale = Locale("es", "ES")

        /**
         * Swedish.
         */
        public val SWEDISH: Locale = Locale("sv", "SE")

        /**
         * Thai.
         */
        public val THAI: Locale = Locale("th")

        /**
         * Turkish.
         */
        public val TURKISH: Locale = Locale("tr")

        /**
         * Ukrainian.
         */
        public val UKRAINIAN: Locale = Locale("uk")

        /**
         * Vietnamese.
         */
        public val VIETNAMESE: Locale = Locale("vi")


        /**
         * All languages [supported by Discord](https://discord.com/developers/docs/reference#Locales).
         */
        public val ALL: List<Locale> = listOf(
            ENGLISH_UNITED_STATES, ENGLISH_GREAT_BRITAN,
            BULGARIAN,
            CHINESE_CHINA, CHINESE_TAIWAN,
            CROATIAN,
            CZECH,
            DANISH,
            DUTCH,
            FINNISH,
            FRENCH,
            GERMAN,
            GREEK,
            HINDI,
            HUNGARIAN,
            ITALIAN,
            JAPENESE,
            KOREAN,
            LITHUANIAN,
            NORWEGIAN,
            POLISH,
            PORTUGUESE_BRAZIL,
            ROMANIAN,
            RUSSIAN,
            SPANISH_SPAIN,
            SWEDISH,
            THAI,
            TURKISH,
            UKRAINIAN,
            VIETNAMESE
        )

        // We accept both "_" and "-" as a separator, because Discord doesn't really document it
        // https://regex101.com/r/fVPdR8/2
        private val languageTagFormat = "(\\w{2})(?:[_\\-](\\w{2}))?".toRegex()

        /**
         * Decodes the language from a `languageCode_countryCode` or `languageCode` format.
         *
         * This does not validate the actually languages and countries, it just validates the format.
         */
        public fun fromString(string: String): Locale {
            val match = languageTagFormat.matchEntire(string) ?: error("$string is not a valid Locale")
            val (language) = match.destructured
            val country = match.groupValues[2]

            return ALL.firstOrNull { (l, c) ->
                language == l && country == (c ?: "")
            } ?: Locale(language, country)
        }
    }

    public object Serializer : KSerializer<Locale> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Locale", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Locale) {
            encoder.encodeString("${value.language}${if (value.country != null) "_${value.country}" else ""}")
        }

        override fun deserialize(decoder: Decoder): Locale = fromString(decoder.decodeString())
    }
}

/**
 * Converts this into a [Locale].
 */
public val JLocale.kLocale: Locale
    get() = Locale(language, country)
