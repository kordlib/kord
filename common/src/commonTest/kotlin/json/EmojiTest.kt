package dev.kord.common.json

import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.common.readFile
import dev.kord.test.IgnoreOnSimulatorPlatforms
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test

private suspend fun file(name: String): String = readFile("emoji", name)

@IgnoreOnSimulatorPlatforms
class EmojiTest {

    @Test
    @JsName("test1")
    fun `Custom Emoji serialization`() = runTest {
        val emoji = Json.decodeFromString(DiscordEmoji.serializer(), file("customemoji"))

        with(emoji) {
            id!!.toString() shouldBe "41771983429993937"
            name shouldBe "LUL"
        }
    }

    @Test
    @JsName("test2")
    fun `Standard Emoji serialization`() = runTest {
        val emoji = Json.decodeFromString(DiscordEmoji.serializer(), file("standardemoji"))

        with(emoji) {
            id shouldBe null
            name shouldBe "🔥"
        }
    }

    @Test
    @JsName("test3")
    fun `Emoji serialization`() = runTest {
        val emoji = Json.decodeFromString(DiscordEmoji.serializer(), file("emoji"))

        with(emoji) {
            id shouldBe "41771983429993937"
            name shouldBe "LUL"
            roles shouldBe listOf("41771983429993000", "41771983429993111").map { Snowflake(it) }
            with(user.value!!) {
                username shouldBe "Luigi"
                discriminator shouldBe "0002"
                globalName shouldBe null
                id shouldBe "96008815106887111"
                avatar shouldBe "5500909a3274e1812beb4e8de6631111"
            }
            requireColons shouldBe true
            managed shouldBe false
            animated shouldBe false
        }
    }
}
