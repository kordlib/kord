package json

import com.gitlab.hopebaron.websocket.*
import com.gitlab.hopebaron.websocket.entity.Snowflake
import kotlinx.serialization.json.Json
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/event/$name.json").readText()
}

class SerializationTest : Spek({

    describe("HeartbeatACK") {
        it("deserializes a HeartbeatACK object") {
            val event = Json.parse(Event.serializer(), file("ack"))
            event shouldBe HeartbeatACK
        }
    }

    describe("Hello") {
        it("deserializes a Hello object") {
            val event = Json.parse(Event.serializer(), file("hello")) as Hello
            with(event) {
                heartbeatInterval shouldBe 1337
                traces shouldBe listOf("test")
            }
        }
    }

    describe("Reconnect") {
        it("deserializes a Reconnect object") {
            val event = Json.parse(Event.serializer(), file("reconnect"))
            event shouldBe Reconnect
        }
    }

    describe("Ready") {
        it("deserializes a Ready object") {
            val event = Json.parse(Event.serializer(), file("ready")) as Ready
            with(event) {
                with(guilds) {
                    val guild = get(0)
                    with(guild) {
                        id shouldBe Snowflake("41771983423143937")
                    }
                    with(user) {
                        id shouldBe Snowflake("80351110224678912")
                        username shouldBe "Nelly"
                        discriminator shouldBe "1337"
                        avatar shouldBe "8342729096ea3675442027381ff50dfe"
                        verified shouldBe true
                        email shouldBe "nelly@discordapp.com"
                        flags shouldBe 64
                        premiumType!!.code shouldBe 1
                    }
                    privateChannels shouldBe listOf()
                    sessionId shouldBe "12345"
                    with(shard!!) {
                        index.shouldBe(0)
                        count.shouldBe(5)
                    }
                    traces shouldBe listOf("test")
                }
            }
        }
    }
    describe("Resumed") {
        it("deserializes a Resumed object") {
            val event = Json.parse(Event.serializer(), file("resumed")) as Resumed
            with(event) { traces shouldBe listOf("kord", "is", "happy") }
        }

    }

    describe("InvalidSession") {
        it("deserializes a InvalidSession object") {
            val event = Json.parse(Event.serializer(), file("invalid")) as InvalidSession
            with(event) { resumable shouldBe false }
        }
    }


    describe("ChannelPinsUpdate") {
        it("deserializes a ChannelPinsUpdate object") {
            val event = Json.parse(Event.serializer(), file("channelpinsupdate")) as ChannelPinsUpdate
            with(event.pins) {
                guildId shouldBe Snowflake("41771983423143937")
                channelId shouldBe Snowflake("399942396007890945")
                lastPinTimestamp shouldBe "2015-04-26T06:26:56.936000+00:00"

            }
        }

    }


    describe("ChannelCreate") {
        it("deserializes a ChannelCreate object") {
            val event = Json.parse(Event.serializer(), file("channelcreate")) as ChannelCreate
            with(event.channel) {
                id shouldBe Snowflake("41771983423143937")
                guildId shouldBe Snowflake("41771983423143937")
                name shouldBe "general"
                type.code shouldBe 0
                position shouldBe 6
                permissionOverwrites shouldBe emptyList()
                rateLimitPerUser shouldBe 2
                nsfw shouldBe true
                topic shouldBe "24/7 chat about how to gank Mike #2"
                lastMessageId shouldBe Snowflake("155117677105512449")
                parentId shouldBe Snowflake("399942396007890945")
            }
        }
    }

    describe("ChannelUpdate") {
        it("deserializes a ChannelUpdate object") {
            val event = Json.parse(Event.serializer(), file("channelupdate")) as ChannelUpdate
            with(event.channel) {
                id shouldBe Snowflake("41771983423143937")
                guildId shouldBe Snowflake("41771983423143937")
                name shouldBe "general"
                type.code shouldBe 0
                position shouldBe 6
                permissionOverwrites shouldBe emptyList()
                rateLimitPerUser shouldBe 2
                nsfw shouldBe true
                topic shouldBe "24/7 chat about how to gank Mike #2"
                lastMessageId shouldBe Snowflake("155117677105512449")
                parentId shouldBe Snowflake("399942396007890945")
            }
        }
    }
    describe("ChannelDelete") {
        it("deserializes a ChannelUpdate object") {
            val event = Json.parse(Event.serializer(), file("channeldelete")) as ChannelDelete
            with(event.channel) {
                id shouldBe Snowflake("41771983423143937")
                guildId shouldBe Snowflake("41771983423143937")
                name shouldBe "general"
                type.code shouldBe 0
                position shouldBe 6
                permissionOverwrites shouldBe emptyList()
                rateLimitPerUser shouldBe 2
                nsfw shouldBe true
                topic shouldBe "24/7 chat about how to gank Mike #2"
                lastMessageId shouldBe Snowflake("155117677105512449")
                parentId shouldBe Snowflake("399942396007890945")
            }
        }
    }
})