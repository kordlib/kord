package dev.kord.common.json

import dev.kord.common.entity.DiscordSoundboardSound
import dev.kord.common.readFile
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test

private suspend fun file(name: String): String = readFile("soundboard", name)

class SoundBoardTest {

    @Test
    @JsName("test1")
    fun `test default soundboard sound serialization`() = runTest {
        val sound = Json.decodeFromString<DiscordSoundboardSound>(file("default"))

        with(sound) {
            name shouldBe "quack"
            soundId shouldBe "1"
            volume shouldBe 1.0
            emojiId shouldBe null
            emojiName shouldBe "🦆"
            available shouldBe true
        }
    }

    @Test
    @JsName("test2")
    fun `test guild soundboard sound serialization`() = runTest {
        val sound = Json.decodeFromString<DiscordSoundboardSound>(file("guild"))

        with(sound) {
            name shouldBe "Yay"
            soundId shouldBe "1106714396018884649"
            volume shouldBe 1.0
            emojiId shouldBe "989193655938064464"
            emojiName shouldBe null
            available shouldBe true
        }
    }
}
