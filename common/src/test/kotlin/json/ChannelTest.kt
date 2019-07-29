@file:Suppress("EXPERIMENTAL_API_USAGE")

package json

import com.gitlab.kordlib.common.entity.Channel
import kotlinx.serialization.json.Json
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/channel/$name.json").readText()
}

class ChannelTest : Spek({

    describe("dmchannel") {
        it("is deserialized correctly") {
            val channel = Json.parse(Channel.serializer(), file("dmchannel"))

            with(channel) {
                lastMessageId shouldBe "3343820033257021450"
                type.code shouldBe 1
                id shouldBe "319674150115610528"
                recipients?.size shouldBe 1
                with(recipients!!.first()) {
                    username shouldBe "test"
                    discriminator shouldBe "9999"
                    id shouldBe "82198898841029460"
                    avatar shouldBe "33ecab261d4681afa4d85a04691c4a01"
                }
            }

        }
    }

    describe("channelcategory") {
        it("is deserialized correctly") {
            val channel = Json.parse(Channel.serializer(), file("channelcategory"))

            with(channel) {
                permissionOverwrites shouldBe emptyList()
                name shouldBe "Test"
                nsfw shouldBe false
                position shouldBe 0
                guildId shouldBe "290926798629997250"
                type.code shouldBe 4
                id shouldBe "399942396007890945"
            }
        }
    }

    describe("groupdmchannel") {
        it("is deserialized correctly") {
            val channel = Json.parse(Channel.serializer(), file("groupdmchannel"))

            with(channel) {
                name shouldBe "Some test channel"
                icon shouldBe null
                recipients!!.size shouldBe 2
                with(recipients!!.first()) {
                    username shouldBe "test"
                    discriminator shouldBe "9999"
                    id shouldBe "82198898841029460"
                    avatar shouldBe "33ecab261d4681afa4d85a04691c4a01"
                }
                with(recipients!![1]) {
                    username shouldBe "test2"
                    discriminator shouldBe "9999"
                    id shouldBe "82198810841029460"
                    avatar shouldBe "33ecab261d4681afa4d85a10691c4a01"
                }
                lastMessageId shouldBe "3343820033257021450"
                type.code shouldBe 3
                id shouldBe "319674150115710528"
                ownerId shouldBe "82198810841029460"
            }
        }
    }

    describe("guildnewschannel") {
        it("is deserialized correctly") {
            val channel = Json.parse(Channel.serializer(), file("guildnewschannel"))

            with(channel) {
                id shouldBe "41771983423143937"
                guildId shouldBe "41771983423143937"
                name shouldBe "important-news"
                type.code shouldBe 5
                position shouldBe 6
                permissionOverwrites shouldBe emptyList()
                nsfw shouldBe true
                topic shouldBe "Rumors about Half Life 3"
                lastMessageId shouldBe "155117677105512449"
                parentId shouldBe "399942396007890945"
            }
        }
    }

    describe("guildtextchannel") {
        it("is deserialized correctly") {
            val channel = Json.parse(Channel.serializer(), file("guildtextchannel"))

            with(channel) {
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

    describe("guildvoicechannel") {
        it("is deserialized correctly") {
            val channel = Json.parse(Channel.serializer(), file("guildvoicechannel"))

            with(channel) {
                id shouldBe "155101607195836416"
                guildId shouldBe "41771983423143937"
                name shouldBe "ROCKET CHEESE"
                type.code shouldBe 2
                nsfw shouldBe false
                position shouldBe 5
                permissionOverwrites shouldBe emptyList()
                bitrate shouldBe 64000
                userLimit shouldBe 0
                parentId shouldBe null
            }
        }
    }

    describe("storechannel") {
        it("is deserialized correctly") {
            val channel = Json.parse(Channel.serializer(), file("storechannel"))

            with(channel) {
                id shouldBe "41771983423143937"
                guildId shouldBe "41771983423143937"
                name shouldBe "buy dota-2"
                type.code shouldBe 6
                position shouldBe 0
                permissionOverwrites shouldBe emptyList()
                nsfw shouldBe false
                parentId shouldBe null
            }
        }
    }
})