package dev.kord.common.json

import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.optional.value
import dev.kord.common.readFile
import dev.kord.test.IgnoreOnSimulatorPlatforms
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.time.Duration.Companion.seconds
import kotlin.test.Test

private suspend fun file(name: String): String = readFile("channel", name)

@IgnoreOnSimulatorPlatforms
class ChannelTest {

    @Test
    @JsName("test1")
    fun `DMChannel serialization`() = runTest {
        val channel = Json.decodeFromString(DiscordChannel.serializer(), file("dmchannel"))

        with(channel) {
            lastMessageId.value!!.toString() shouldBe "3343820033257021450"
            type.value shouldBe 1
            id.toString() shouldBe "319674150115610528"
            recipients.value!!.size shouldBe 1
            with(recipients.value!!.first()) {
                username shouldBe "test"
                discriminator shouldBe "9999"
                globalName shouldBe null
                id.toString() shouldBe "82198898841029460"
                avatar shouldBe "33ecab261d4681afa4d85a04691c4a01"
            }
        }

    }


    @Test
    @JsName("test2")
    fun `ChannelCategory serialization`() = runTest {
        val channel = Json.decodeFromString(DiscordChannel.serializer(), file("channelcategory"))

        with(channel) {
            permissionOverwrites.value shouldBe emptyList()
            name.value shouldBe "Test"
            nsfw.asNullable shouldBe false
            position.value shouldBe 0
            guildId.value?.toString() shouldBe "290926798629997250"
            type.value shouldBe 4
            id.toString() shouldBe "399942396007890945"
        }
    }


    @Test
    @JsName("test3")
    fun `GroupDMChannel serialization`() = runTest {
        val channel = Json.decodeFromString(DiscordChannel.serializer(), file("groupdmchannel"))

        with(channel) {
            name.value shouldBe "Some test channel"
            icon.value shouldBe null
            recipients.value!!.size shouldBe 2
            with(recipients.value!!.first()) {
                username shouldBe "test"
                discriminator shouldBe "9999"
                globalName shouldBe null
                id.toString() shouldBe "82198898841029460"
                avatar shouldBe "33ecab261d4681afa4d85a04691c4a01"
            }
            with(recipients.value!![1]) {
                username shouldBe "test2"
                discriminator shouldBe "9999"
                globalName shouldBe "amazing name"
                id.toString() shouldBe "82198810841029460"
                avatar shouldBe "33ecab261d4681afa4d85a10691c4a01"
            }
            lastMessageId.value?.toString() shouldBe "3343820033257021450"
            type.value shouldBe 3
            id.toString() shouldBe "319674150115710528"
            ownerId.value?.toString() shouldBe "82198810841029460"
        }
    }


    @Test
    @JsName("test4")
    fun `GuildNewChannel serialization`() = runTest {
        val channel = Json.decodeFromString(DiscordChannel.serializer(), file("guildnewschannel"))

        with(channel) {
            id.toString() shouldBe "41771983423143937"
            guildId.value!!.toString() shouldBe "41771983423143937"
            name.value shouldBe "important-news"
            type.value shouldBe 5
            position.value shouldBe 6
            permissionOverwrites.value!! shouldBe emptyList()
            nsfw.value shouldBe true
            topic.value shouldBe "Rumors about Half Life 3"
            lastMessageId.value?.toString() shouldBe "155117677105512449"
            parentId.value?.toString() shouldBe "399942396007890945"
        }
    }


    @Test
    @JsName("test5")
    fun `GuildTextChannel serialization`() = runTest {
        val channel = Json.decodeFromString(DiscordChannel.serializer(), file("guildtextchannel"))

        with(channel) {
            id.toString() shouldBe "41771983423143937"
            guildId.value!!.toString() shouldBe "41771983423143937"
            name.value shouldBe "general"
            type.value shouldBe 0
            position.asNullable!! shouldBe 6
            permissionOverwrites.value shouldBe emptyList()
            rateLimitPerUser.value shouldBe 2.seconds
            nsfw.value shouldBe true
            topic.value shouldBe "24/7 chat about how to gank Mike #2"
            lastMessageId.value?.toString() shouldBe "155117677105512449"
            parentId.value?.toString() shouldBe "399942396007890945"
        }
    }


    @Test
    @JsName("test6")
    fun `GuildVoiceChannel serialization`() = runTest {
        val channel = Json.decodeFromString(DiscordChannel.serializer(), file("guildvoicechannel"))

        with(channel) {
            id.toString() shouldBe "155101607195836416"
            guildId.value!!.toString() shouldBe "41771983423143937"
            name.value shouldBe "ROCKET CHEESE"
            type.value shouldBe 2
            nsfw.asNullable shouldBe false
            position.asNullable shouldBe 5
            permissionOverwrites.value!! shouldBe emptyList()
            bitrate.value shouldBe 64000
            userLimit.value shouldBe 0
            parentId.value?.toString() shouldBe null
        }
    }
}
