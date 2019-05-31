package json

import com.gitlab.hopebaron.websocket.entity.Emoji
import com.gitlab.hopebaron.websocket.entity.Snowflake
import kotlinx.serialization.json.Json
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/emoji/$name.json").readText()
}

class EmojiTest : Spek({

    describe("customemoji") {
        it("is deserialized correctly") {
            val emoji = Json.parse(Emoji.serializer(), file("customemoji"))

            with(emoji) {
                id shouldBe Snowflake("41771983429993937")
                name shouldBe "LUL"
            }

        }
    }

    describe("standardemoji") {
        it("is deserialized correctly") {
            val emoji = Json.parse(Emoji.serializer(), file("standardemoji"))

            with(emoji) {
                id shouldBe null
                name shouldBe "🔥"
            }

        }
    }

    describe("emoji") {
        it("is deserialized correctly") {
            val emoji = Json.parse(Emoji.serializer(), file("emoji"))

            with(emoji) {
                id shouldBe Snowflake("41771983429993937")
                name shouldBe "LUL"
                roles shouldBe listOf("41771983429993000", "41771983429993111")
                with(user!!) {
                    username shouldBe "Luigi"
                    discriminator shouldBe "0002"
                    id shouldBe Snowflake("96008815106887111")
                    avatar shouldBe "5500909a3274e1812beb4e8de6631111"
                }
                requireColons shouldBe true
                managed shouldBe false
                animated shouldBe false
            }

        }
    }

})