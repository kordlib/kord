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
 * Representation of a locale.
 *
 * @property language an ISO 369 language code representing the language
 * @property country an ISO 3166 country code representing the country
 */
@Serializable(with = Locale.Serializer::class)
data class Locale(val language: String, val country: String?) {
    /**
     * Converts this into a [JLocale].
     */
    fun asJavaLocale() = JLocale(language, country ?: "")

    companion object {
        // We accept both "_" and "-" as a separator, because Discord doesn't really document it
        // https://regex101.com/r/fVPdR8/2
        private val languageTagFormat = "(\\w{2})(?:[_\\-](\\w{2}))?".toRegex()

        /**
         * Decodes the language from a `languageCode_countryCode` or `languageCode` format.
         *
         * This doesn't validate whether the language code is actually valid
         */
        fun fromString(string: String): Locale {
            val match = languageTagFormat.matchEntire(string) ?: error("$string is not a valid Locale")

            val (language) = match.destructured
            val country = match.groupValues.getOrNull(2)

            return Locale(language, country)
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
