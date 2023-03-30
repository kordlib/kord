package dev.kord.gateway.json

import dev.kord.common.entity.UserFlags
import dev.kord.common.entity.UserPremium
import dev.kord.common.entity.optional.value
import dev.kord.gateway.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

private suspend fun file(name: String): String = readFile("event", name)

class SerializationTest {

    @Test
    @JsName("test1")
    fun `HeartbeatACK Event serialization`() = runTest {
        val event = Json.decodeFromString(Event.DeserializationStrategy, file("ack"))
        event shouldBe HeartbeatACK
    }


    @Test
    @JsName("test2")
    fun `Hello Event serialization`() = runTest {
        val event = Json.decodeFromString(Event.DeserializationStrategy, file("hello")) as Hello
        with(event) {
            heartbeatInterval shouldBe 1337
        }
    }


    @Test
    @JsName("test3")
    fun `Reconnect Event serialization`() = runTest {
        val event = Json.decodeFromString(Event.DeserializationStrategy, file("reconnect"))
        event shouldBe Reconnect
    }


    @Test
    @JsName("test4")
    fun `Ready Event serialization`() = runTest {
        val event = Json.decodeFromString(Event.DeserializationStrategy, file("ready")) as Ready
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
                resumeGatewayUrl shouldBe "wss://us-east1-b.gateway.discord.gg"
                with(shard.value!!) {
                    index.shouldBe(0)
                    count.shouldBe(5)
                }
                traces shouldBe listOf("test")
            }
        }
    }

    @Test
    @JsName("test5")
    fun `Resumed Event serialization`() = runTest {
        Json.decodeFromString(Event.DeserializationStrategy, file("resumed")) as Resumed
    }


    @Test
    @JsName("test6")
    fun `InvalidSession command serialization`() = runTest {
        val event = Json.decodeFromString(Event.DeserializationStrategy, file("invalid")) as InvalidSession
        with(event) { resumable shouldBe false }
    }


    @Test
    @JsName("test7")
    fun `ChannelPinsUpdate Event serialization`() = runTest {
        val event = Json.decodeFromString(Event.DeserializationStrategy, file("channelpinsupdate")) as ChannelPinsUpdate
        with(event.pins) {
            guildId.value?.toString() shouldBe "41771983423143937"
            channelId.toString() shouldBe "399942396007890945"
            lastPinTimestamp.value shouldBe Instant.parse("2015-04-26T06:26:56.936000+00:00")

        }
    }


    @Test
    @JsName("test8")
    fun `ChannelCreate Event serialization`() = runTest {
        val event = Json.decodeFromString(Event.DeserializationStrategy, file("channelcreate")) as ChannelCreate
        with(event.channel) {
            id.toString() shouldBe "41771983423143937"
            guildId.value?.toString() shouldBe "41771983423143937"
            name.value shouldBe "general"
            type.value shouldBe 0
            position.value shouldBe 6
            permissionOverwrites.value shouldBe emptyList()
            rateLimitPerUser.value shouldBe 2.seconds
            nsfw.value shouldBe true
            topic.value shouldBe "24/7 chat about how to gank Mike #2"
            lastMessageId.value?.toString() shouldBe "155117677105512449"
            parentId.value?.toString() shouldBe "399942396007890945"
        }
    }


    @Test
    @JsName("test9")
    fun `ChannelUpdate Event serialization`() = runTest {
        val event = Json.decodeFromString(Event.DeserializationStrategy, file("channelupdate")) as ChannelUpdate
        with(event.channel) {
            id.toString() shouldBe "41771983423143937"
            guildId.value?.toString() shouldBe "41771983423143937"
            name.value shouldBe "general"
            type.value shouldBe 0
            position.value shouldBe 6
            permissionOverwrites.value shouldBe emptyList()
            rateLimitPerUser.value shouldBe 2.seconds
            nsfw.value shouldBe true
            topic.value shouldBe "24/7 chat about how to gank Mike #2"
            lastMessageId.value?.toString() shouldBe "155117677105512449"
            parentId.value?.toString() shouldBe "399942396007890945"
        }
    }

    @Test
    @JsName("test10")
    fun `ChannelDelete Event serialization`() = runTest {
        val event = Json.decodeFromString(Event.DeserializationStrategy, file("channeldelete")) as ChannelDelete
        with(event.channel) {
            id.toString() shouldBe "41771983423143937"
            guildId.value?.toString() shouldBe "41771983423143937"
            name.value shouldBe "general"
            type.value shouldBe 0
            position.value shouldBe 6
            permissionOverwrites.value shouldBe emptyList()
            rateLimitPerUser.value shouldBe 2.seconds
            nsfw.value shouldBe true
            topic.value shouldBe "24/7 chat about how to gank Mike #2"
            lastMessageId.value?.toString() shouldBe "155117677105512449"
            parentId?.value?.toString() shouldBe "399942396007890945"
        }
    }

}
