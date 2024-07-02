package dev.kord.common.json

import dev.kord.common.entity.*
import dev.kord.common.readFile
import dev.kord.test.IgnoreOnSimulatorPlatforms
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test

private suspend fun file(name: String): String = readFile("message", name)

@IgnoreOnSimulatorPlatforms
class MessageTest {

    @Test
    @JsName("test1")
    fun `Message serialization`() = runTest {
        val message: DiscordMessage = Json.decodeFromString(DiscordMessage.serializer(), file("message"))

        with(message) {
            reactions.value!!.size shouldBe 1
            with(reactions.value!!.first()) {
                count shouldBe 1
                me shouldBe false
                with(emoji) {
                    id shouldBe null
                    name shouldBe "🔥"
                }
            }
            attachments shouldBe emptyList()
            tts shouldBe false
            embeds shouldBe emptyList()
            timestamp shouldBe Instant.parse("2017-07-11T17:27:07.299000+00:00")
            mentionEveryone shouldBe false
            id.toString() shouldBe "334385199974967042"
            pinned shouldBe false
            editedTimestamp shouldBe null
            with(author) {
                username shouldBe "Mason"
                discriminator shouldBe "9999"
                globalName shouldBe null
                id shouldBe "53908099506183680"
                avatar shouldBe "a_bab14f271d565501444b2ca3be944b25"
            }
            mentionRoles shouldBe emptyList()
            content shouldBe "Supa Hot"
            channelId shouldBe "290926798999357250"
            mentions shouldBe emptyList()
            type.code shouldBe 0
            nonce shouldBe "1676368321197"
        }
    }

    @Test
    @JsName("test2")
    fun `User serialization`() = runTest {
        val message = Json.decodeFromString(DiscordMessage.serializer(), file("crossposted"))

        with(message) {
            reactions.value!!.size shouldBe 1
            with(reactions.value!!.first()) {
                count shouldBe 1
                me shouldBe false
                with(emoji) {
                    id shouldBe null
                    name shouldBe "🔥"
                }
            }
            attachments shouldBe emptyList()
            tts shouldBe false
            embeds shouldBe emptyList()
            timestamp shouldBe Instant.parse("2017-07-11T17:27:07.299000+00:00")
            mentionEveryone shouldBe false
            id.toString() shouldBe "334385199974967042"
            pinned shouldBe false
            editedTimestamp shouldBe null
            with(author) {
                username shouldBe "Mason"
                discriminator shouldBe "9999"
                globalName shouldBe "Mason!"
                id.toString() shouldBe "53908099506183680"
                avatar shouldBe "a_bab14f271d565501444b2ca3be944b25"
            }
            mentionRoles shouldBe emptyList()
            content shouldBe "Big news! In this <#278325129692446722> channel!"
            channelId.toString() shouldBe "290926798999357250"
            mentions shouldBe emptyList()
            type.code shouldBe 0
            flags shouldBe MessageFlags(MessageFlag.IsCrossPost.code)
            with(messageReference.value!!) {
                channelId.value?.toString() shouldBe "278325129692446722"
                guildId.value!!.toString() shouldBe "278325129692446720"
                id.value!!.toString() shouldBe "306588351130107906"
            }
        }
    }
}
