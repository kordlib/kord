package json

import com.gitlab.hopebaron.websocket.*
import com.gitlab.hopebaron.websocket.entity.Shard
import com.gitlab.hopebaron.websocket.entity.Snowflake
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json
import kotlinx.serialization.json.jsonArray
import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CommandTest : Spek({

    describe("Resume") {
        it("is serialized correctly") {
            val token = "token"
            val sessionId = "session"
            val sequence = 1337L

            val resume = Json.stringify(Resume.Companion, Resume(token, sessionId, sequence))

            val json = Json.stringify(JsonObject.serializer(), json {
                "op" to OpCode.Resume.code
                "d" to json {
                    "token" to token
                    "session_id" to sessionId
                    "seq" to sequence
                }
            })

            assertEquals(json, resume)
        }
    }


    describe("Heartbeat") {
        it("is serialized correctly") {
            val interval = 1337L

            val heartbeat = Json.stringify(Heartbeat.Companion, Heartbeat(interval))

            val json = Json.stringify(JsonObject.serializer(), json {
                "op" to OpCode.Heartbeat.code
                "d" to interval
            })

            assertEquals(json, heartbeat)
        }
    }

    describe("RequestGuildMembers") {
        it("is serialized correctly") {
            val guildId = Snowflake("1337")
            val query = "test"
            val limit = 1337

            val request = Json.stringify(Command.Companion, RequestGuildMembers(guildId, query, limit))

            val json = Json.stringify(JsonObject.serializer(), json {
                "op" to OpCode.RequestGuildMembers.code
                "d" to json {
                    "guild_id" to guildId.id
                    "query" to query
                    "limit" to limit
                }
            })

            assertEquals(json, request)
        }
    }

    describe("UpdateVoiceStatus") {
        it("is serialized correctly") {
            val guildId = Snowflake("1337")
            val channelId = Snowflake("420")
            val selfMute = true
            val selfDeaf = false

            val status = Json.stringify(Command.Companion, UpdateVoiceStatus(guildId, channelId, selfMute, selfDeaf))

            val json = Json.stringify(JsonObject.serializer(), json {
                "op" to OpCode.VoiceStateUpdate.code
                "d" to json {
                    "guild_id" to guildId.id
                    "channel_id" to channelId.id
                    "self_mute" to selfMute
                    "self_deaf" to selfDeaf
                }
            })

            assertEquals(json, status)
        }
    }

    describe("UpdateStatus") {
        it("is serialized correctly") {
            val since = 1242518400L
            val game = null
            val status = Status.Online
            val afk = false

            val updateStatus = Json.stringify(Command.Companion, UpdateStatus(since, game, status, afk))

            val json = Json.stringify(JsonObject.serializer(), json {
                "op" to OpCode.StatusUpdate.code
                "d" to json {
                    "since" to since
                    "game" to null as String?
                    "status" to status.name.toLowerCase()
                    "afk" to afk
                }
            })

            assertEquals(json, updateStatus)
        }
    }

    describe("Identify") {

        it("is serialized correctly") {
            val token = "test"
            val properties = IdentifyProperties("os", "browser", "device")
            val compress = false
            val largeThreshold = 1337
            val shard = Shard(0, 1)
            val presence = null

            val identify = Json.stringify(Identify.Companion, Identify(token, properties, compress, largeThreshold, shard, presence))

            val json = Json.stringify(JsonObject.serializer(), json {
                "op" to OpCode.Identify.code
                "d" to json {
                    "token" to token
                    "properties" to json {
                        "\$os" to "os"
                        "\$browser" to "browser"
                        "\$device" to "device"
                    }
                    "compress" to compress
                    "large_threshold" to largeThreshold
                    "shard" to jsonArray {
                        +0
                        +1
                    }
                    "presence" to null as String?
                }
            })

            assertEquals(json, identify)
        }

    }

})