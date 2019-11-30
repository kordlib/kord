@file:Suppress("EXPERIMENTAL_API_USAGE")

package json

import com.gitlab.kordlib.common.entity.DiscordShard
import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.gateway.*
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CommandTest {
    @Test
    fun `Resume command serialization`() {
        val token = "token"
        val sessionId = "session"
        val sequence = 1337

        val resume = Json.stringify(Command.Companion, Resume(token, sessionId, sequence))

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


    @Test
    fun `Heartbeat command serialization`() {
        val interval = 1337

        val heartbeat = Json.stringify(Command.Companion, Command.Heartbeat(interval))

        val json = Json.stringify(JsonObject.serializer(), json {
            "op" to OpCode.Heartbeat.code
            "d" to interval
        })

        assertEquals(json, heartbeat)
    }


    @Test
    fun `RequestGuildMembers command serialization`() {
        val guildId = "1337"
        val query = "test"
        val limit = 1337

        val request = Json.stringify(Command.Companion, RequestGuildMembers(listOf(guildId), query, limit))

        val json = Json.stringify(JsonObject.serializer(), json {
            "op" to OpCode.RequestGuildMembers.code
            "d" to json {
                "guild_id" to jsonArray {
                    +guildId
                }
                "query" to query
                "limit" to limit
                "presences" to null as Int?
                "user_ids" to null as Int?
            }
        })

        assertEquals(json, request)
    }


    @Test
    fun `UpdateVoiceState command serialization`() {
        val guildId = "1337"
        val channelId = "420"
        val selfMute = true
        val selfDeaf = false

        val status = Json.stringify(Command.Companion, UpdateVoiceStatus(guildId, channelId, selfMute, selfDeaf))

        val json = Json.stringify(JsonObject.serializer(), json {
            "op" to OpCode.VoiceStateUpdate.code
            "d" to json {
                "guild_id" to guildId
                "channel_id" to channelId
                "self_mute" to selfMute
                "self_deaf" to selfDeaf
            }
        })

        assertEquals(json, status)
    }


    @Test
    fun `UpdateState command serialization`() {
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


    @Test
    fun `Identify command serialization`() {
        val token = "test"
        val properties = IdentifyProperties("os", "browser", "device")
        val compress = false
        val largeThreshold = 1337
        val shard = DiscordShard(0, 1)
        val presence = null

        val identify = Json.stringify(Command.Companion, Identify(token, properties, compress, largeThreshold, shard, presence))

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
                    +JsonLiteral(0)
                    +JsonLiteral(1)
                }
                "presence" to null as String?
            }
        })

        assertEquals(json, identify)
    }
}