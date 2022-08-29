import dev.kord.common.Locale
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class LocaleTest {

    private val all = with(Locale) {
        listOf(
            "da" to DANISH,
            "de" to GERMAN,
            "en-GB" to ENGLISH_GREAT_BRITAIN,
            "en-US" to ENGLISH_UNITED_STATES,
            "es-ES" to SPANISH_SPAIN,
            "fr" to FRENCH,
            "hr" to CROATIAN,
            "it" to ITALIAN,
            "lt" to LITHUANIAN,
            "hu" to HUNGARIAN,
            "nl" to DUTCH,
            "no" to NORWEGIAN,
            "pl" to POLISH,
            "pt-BR" to PORTUGUESE_BRAZIL,
            "ro" to ROMANIAN,
            "fi" to FINNISH,
            "sv-SE" to SWEDISH,
            "vi" to VIETNAMESE,
            "tr" to TURKISH,
            "cs" to CZECH,
            "el" to GREEK,
            "bg" to BULGARIAN,
            "ru" to RUSSIAN,
            "uk" to UKRAINIAN,
            "hi" to HINDI,
            "th" to THAI,
            "zh-CN" to CHINESE_CHINA,
            "ja" to JAPANESE,
            "zh-TW" to CHINESE_TAIWAN,
            "ko" to KOREAN,
        )
    }

    init {
        require(all.size == Locale.ALL.size)
    }


    @Test
    fun `all documented Locales can be deserialized`() {
        all.forEach { (string, locale) ->
            assertEquals(expected = locale, actual = Json.decodeFromString("\"$string\""))
        }
    }

    @Test
    fun `all documented Locales can be serialized`() {
        all.forEach { (string, locale) ->
            assertEquals(expected = "\"$string\"", actual = Json.encodeToString(locale))
        }
    }
}
