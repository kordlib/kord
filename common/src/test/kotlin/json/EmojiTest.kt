package json

import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test


private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/emoji/$name.json").readText()
}

class EmojiTest {

    @Test
    fun `Custom Emoji serialization`() {
        val emoji = Json.decodeFromString(DiscordEmoji.serializer(), file("customemoji"))

        with(emoji) {
            id!!.toString() shouldBe "41771983429993937"
            name shouldBe "LUL"
        }
    }

    @Test
    fun `Standard Emoji serialization`() {
        val emoji = Json.decodeFromString(DiscordEmoji.serializer(), file("standardemoji"))

        with(emoji) {
            id shouldBe null
            name shouldBe "🔥"
        }
    }

    @Test
    fun `Emoji serialization`() {
        val emoji = Json.decodeFromString(DiscordEmoji.serializer(), file("emoji"))

        with(emoji) {
            id shouldBe "41771983429993937"
            name shouldBe "LUL"
            roles shouldBe listOf("41771983429993000", "41771983429993111").map { Snowflake(it) }
            with(user.value!!) {
                username shouldBe "Luigi"
                discriminator shouldBe "0002"
                id shouldBe "96008815106887111"
                avatar shouldBe "5500909a3274e1812beb4e8de6631111"
            }
            requireColons shouldBe true
            managed shouldBe false
            animated shouldBe false
        }
    }
}
