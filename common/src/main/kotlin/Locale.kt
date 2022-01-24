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
 * @property language an ISO 639-2 language code representing the language
 * @property country an ISO 3166 country code representing the country
 */
@Serializable(with = Locale.Serializer::class)
data class Locale(val language: String, val country: String? = null) {
    /**
     * Converts this into a [JLocale].
     */
    fun asJavaLocale() = JLocale(language, country ?: "")

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {

        /**
         * English (United States).
         */
        val ENGLISH_UNITED_STATES = Locale("en", "US")

        /**
         * English (Great Britain).
         */
        val ENGLISH_GREAT_BRITAN = Locale("en", "GB")

        /**
         * Bulgarian.
         */
        val BULGARIAN = Locale("bg")

        /**
         * Chinese (China).
         */
        val CHINESE_CHINA = Locale("zh", "CN")

        /**
         * Chinese (Taiwan).
         */
        val CHINESE_TAIWAN = Locale("zh", "TW")

        /**
         * Croatian.
         */
        val CROATIAN = Locale("hr")

        /**
         * Czech.
         */
        val CZECH = Locale("cs")

        /**
         * Danish.
         */
        val DANISH = Locale("da")

        /**
         * Dutch.
         */
        val DUTCH = Locale("nl")

        /**
         * Finnish.
         */
        val FINNISH = Locale("fi")

        /**
         * French.
         */
        val FRENCH = Locale("fr")

        /**
         * German.
         */
        val GERMAN = Locale("de")

        /**
         * Greek.
         */
        val GREEK = Locale("el")

        /**
         * Hindi.
         */
        val HINDI = Locale("hi")

        /**
         * Hungarian.
         */
        val HUNGARIAN = Locale("hu")

        /**
         * Italian.
         */
        val ITALIAN = Locale("it")

        /**
         * Japanese.
         */
        val JAPENESE = Locale("ja")

        /**
         * Korean.
         */
        val KOREAN = Locale("ko")

        /**
         * Lithuanian.
         */
        val LITHUANIAN = Locale("lt")

        /**
         * Norwegian.
         */
        val NORWEGIAN = Locale("no")

        /**
         * Polish.
         */
        val POLISH = Locale("pl")

        /**
         * Portuguese (Brazil).
         */
        val PORTUGUESE_BRAZIL = Locale("pt", "BR")

        /**
         * Romanian.
         */
        val ROMANIAN = Locale("ro")

        /**
         * Russian.
         */
        val RUSSIAN = Locale("ru")

        /**
         * Spanish (Spain).
         */
        val SPANISH_SPAIN = Locale("es", "ES")

        /**
         * Swedish.
         */
        val SWEDISH = Locale("sv", "SE")

        /**
         * Thai.
         */
        val THAI = Locale("th")

        /**
         * Turkish.
         */
        val TURKISH = Locale("tr")

        /**
         * Ukrainian.
         */
        val UKRAINIAN = Locale("uk")

        /**
         * Vietnamese.
         */
        val VIETNAMESE = Locale("vi")


        /**
         * All languages [supported by Discord](https://discord.com/developers/docs/reference#Locales).
         */
        val ALL = listOf(
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
        fun fromString(string: String): Locale {
            val match = languageTagFormat.matchEntire(string) ?: error("$string is not a valid Locale")
            val (language) = match.destructured
            val country = match.groupValues[2]

            return ALL.firstOrNull { (l, c) ->
                language == l && country == (c ?: "")
            } ?: Locale(language, country)
        }
    }

    object Serializer : KSerializer<Locale> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Locale", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Locale) {
            encoder.encodeString("${value.language}${if(value.country != null) "_${value.country}" else ""}")
        }

        override fun deserialize(decoder: Decoder): Locale = fromString(decoder.decodeString())
    }
}

/**
 * Converts this into a [Locale].
 */
val JLocale.kLocale: Locale
    get() = Locale(language, country)
