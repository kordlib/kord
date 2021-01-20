package json

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordInteraction
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

private fun file(name: String): String {
    val loader = InteractionTest::class.java.classLoader
    return loader.getResource("json/interaction/$name.json")!!.readText()
}

@OptIn(KordPreview::class)
class InteractionTest {

    val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `DiscordInteraction can be deserialized`() {
        val text = file("interaction")

        json.decodeFromString(DiscordInteraction.serializer(), text)
    }

}