@file:Suppress("EXPERIMENTAL_API_USAGE")

package json

import com.gitlab.kordlib.gateway.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

private fun file(name: String): String {
    val loader = SerializationTest::class.java.classLoader
    return loader.getResource("json/event/$name.json").readText()
}

class SerializationTest {

    @Test
    fun `HeartbeatACK Event serialization`() {
        val event = Json.parse(Event.Companion, file("ack"))
        event shouldBe HeartbeatACK
    }


    @Test
    fun `Hello Event serialization`() {
        val event = Json.parse(Event.Companion, file("hello")) as Hello
        with(event) {
            heartbeatInterval shouldBe 1337
            traces shouldBe listOf("test")
        }
    }


    @Test
    fun `Reconnect Event serialization`() {
        val event = Json.parse(Event.Companion, file("reconnect"))
        event shouldBe Reconnect
    }


    @Test
    fun `Ready Event serialization`() {
        val event = Json.parse(Event.Companion, file("ready")) as Ready
        with(event.data) {
            with(event.data.guilds) {
                val guild = get(0)
                with(guild) {
                    id shouldBe "41771983423143937"
                }
                with(user) {
                    id shouldBe "80351110224678912"
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

    @Test
    fun `Resumed Event serialization`() {
        val event = Json.parse(Event.Companion, file("resumed")) as Resumed
        with(event) { data.traces shouldBe listOf("kord", "is", "happy") }
    }


    @Test
    fun `InvalidSession command serialization`() {
        val event = Json.parse(Event.Companion, file("invalid")) as InvalidSession
        with(event) { resumable shouldBe false }
    }


    @Test
    fun `ChannelPinsUpdate Event serialization`() {
        val event = Json.parse(Event.Companion, file("channelpinsupdate")) as ChannelPinsUpdate
        with(event.pins) {
            guildId shouldBe "41771983423143937"
            channelId shouldBe "399942396007890945"
            lastPinTimestamp shouldBe "2015-04-26T06:26:56.936000+00:00"

        }
    }


    @Test
    fun `ChannelCreate Event serialization`() {
        val event = Json.parse(Event.Companion, file("channelcreate")) as ChannelCreate
        with(event.channel) {
            id shouldBe "41771983423143937"
            guildId shouldBe "41771983423143937"
            name shouldBe "general"
            type.code shouldBe 0
            position shouldBe 6
            permissionOverwrites shouldBe emptyList()
            rateLimitPerUser shouldBe 2
            nsfw shouldBe true
            topic shouldBe "24/7 chat about how to gank Mike #2"
            lastMessageId shouldBe "155117677105512449"
            parentId shouldBe "399942396007890945"
        }
    }


    @Test
    fun `ChannelUpdate Event serialization`() {
        val event = Json.parse(Event.Companion, file("channelupdate")) as ChannelUpdate
        with(event.channel) {
            id shouldBe "41771983423143937"
            guildId shouldBe "41771983423143937"
            name shouldBe "general"
            type.code shouldBe 0
            position shouldBe 6
            permissionOverwrites shouldBe emptyList()
            rateLimitPerUser shouldBe 2
            nsfw shouldBe true
            topic shouldBe "24/7 chat about how to gank Mike #2"
            lastMessageId shouldBe "155117677105512449"
            parentId shouldBe "399942396007890945"
        }
    }

    @Test
    fun `ChannelDelete Event serialization`() {
        val event = Json.parse(Event.Companion, file("channeldelete")) as ChannelDelete
        with(event.channel) {
            id shouldBe "41771983423143937"
            guildId shouldBe "41771983423143937"
            name shouldBe "general"
            type.code shouldBe 0
            position shouldBe 6
            permissionOverwrites shouldBe emptyList()
            rateLimitPerUser shouldBe 2
            nsfw shouldBe true
            topic shouldBe "24/7 chat about how to gank Mike #2"
            lastMessageId shouldBe "155117677105512449"
            parentId shouldBe "399942396007890945"
        }
    }

}