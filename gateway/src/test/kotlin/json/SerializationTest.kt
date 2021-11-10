@file:Suppress("EXPERIMENTAL_API_USAGE")

package json

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.UserFlags
import dev.kord.common.entity.UserPremium
import dev.kord.common.entity.optional.value
import dev.kord.gateway.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

private fun file(name: String): String {
    val loader = SerializationTest::class.java.classLoader
    return loader.getResource("json/event/$name.json").readText()
}

class SerializationTest {

    @Test
    fun `HeartbeatACK Event serialization`() {
        val event = Json.decodeFromString(Event.Companion, file("ack"))
        event shouldBe HeartbeatACK
    }


    @Test
    fun `Hello Event serialization`() {
        val event = Json.decodeFromString(Event.Companion, file("hello")) as Hello
        with(event) {
            heartbeatInterval shouldBe 1337
        }
    }


    @Test
    fun `Reconnect Event serialization`() {
        val event = Json.decodeFromString(Event.Companion, file("reconnect"))
        event shouldBe Reconnect
    }


    @Test
    fun `Ready Event serialization`() {
        val event = Json.decodeFromString(Event.Companion, file("ready")) as Ready
        with(event.data) {
            with(event.data.guilds) {
                val guild = get(0)
                with(guild) {
                    id.toString() shouldBe "41771983423143937"
                }
                with(user) {
                    id.toString() shouldBe "80351110224678912"
                    username shouldBe "Nelly"
                    discriminator shouldBe "1337"
                    avatar shouldBe "8342729096ea3675442027381ff50dfe"
                    verified.value shouldBe true
                    email.value shouldBe "nelly@discordapp.com"
                    flags.value shouldBe UserFlags(64)
                    premiumType.value shouldBe UserPremium.NitroClassic
                }
                privateChannels shouldBe listOf()
                sessionId shouldBe "12345"
                with(shard.value!!) {
                    index.shouldBe(0)
                    count.shouldBe(5)
                }
                traces shouldBe listOf("test")
            }
        }
    }

    @Test
    fun `Resumed Event serialization`() {
        Json.decodeFromString(Event.Companion, file("resumed")) as Resumed
    }


    @Test
    fun `InvalidSession command serialization`() {
        val event = Json.decodeFromString(Event.Companion, file("invalid")) as InvalidSession
        with(event) { resumable shouldBe false }
    }


    @Test
    fun `ChannelPinsUpdate Event serialization`() {
        val event = Json.decodeFromString(Event.Companion, file("channelpinsupdate")) as ChannelPinsUpdate
        with(event.pins) {
            guildId.value?.toString() shouldBe "41771983423143937"
            channelId.toString() shouldBe "399942396007890945"
            lastPinTimestamp.value shouldBe "2015-04-26T06:26:56.936000+00:00"

        }
    }


    @Test
    fun `ChannelCreate Event serialization`() {
        val event = Json.decodeFromString(Event.Companion, file("channelcreate")) as ChannelCreate
        with(event.channel) {
            id.toString() shouldBe "41771983423143937"
            guildId.value?.toString() shouldBe "41771983423143937"
            name.value shouldBe "general"
            type.value shouldBe 0
            position.value shouldBe 6
            permissionOverwrites.value shouldBe emptyList()
            rateLimitPerUser.value shouldBe 2
            nsfw.value shouldBe true
            topic.value shouldBe "24/7 chat about how to gank Mike #2"
            lastMessageId.value?.toString() shouldBe "155117677105512449"
            parentId.value?.toString() shouldBe "399942396007890945"
        }
    }


    @Test
    fun `ChannelUpdate Event serialization`() {
        val event = Json.decodeFromString(Event.Companion, file("channelupdate")) as ChannelUpdate
        with(event.channel) {
            id.toString() shouldBe "41771983423143937"
            guildId.value?.toString() shouldBe "41771983423143937"
            name.value shouldBe "general"
            type.value shouldBe 0
            position.value shouldBe 6
            permissionOverwrites.value shouldBe emptyList()
            rateLimitPerUser.value shouldBe 2
            nsfw.value shouldBe true
            topic.value shouldBe "24/7 chat about how to gank Mike #2"
            lastMessageId.value?.toString() shouldBe "155117677105512449"
            parentId.value?.toString() shouldBe "399942396007890945"
        }
    }

    @Test
    fun `ChannelDelete Event serialization`() {
        val event = Json.decodeFromString(Event.Companion, file("channeldelete")) as ChannelDelete
        with(event.channel) {
            id.toString() shouldBe "41771983423143937"
            guildId.value?.toString() shouldBe "41771983423143937"
            name.value shouldBe "general"
            type.value shouldBe 0
            position.value shouldBe 6
            permissionOverwrites.value shouldBe emptyList()
            rateLimitPerUser.value shouldBe 2
            nsfw.value shouldBe true
            topic.value shouldBe "24/7 chat about how to gank Mike #2"
            lastMessageId.value?.toString() shouldBe "155117677105512449"
            parentId?.value?.toString() shouldBe "399942396007890945"
        }
    }

}
