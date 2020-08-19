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

        val resume = Json.encodeToString(Command.Companion, Resume(token, sessionId, sequence))

        val json = Json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.Resume.code)
            put("d", buildJsonObject {
                put("token", token)
                put("session_id", sessionId)
                put("seq", sequence)
            })
        })

        assertEquals(json, resume)
    }


    @Test
    fun `Heartbeat command serialization`() {
        val interval = 1337

        val heartbeat = Json.encodeToString(Command.Companion, Command.Heartbeat(interval))

        val json = Json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.Heartbeat.code)
            put("d", interval)
        })

        assertEquals(json, heartbeat)
    }


    @Test
    fun `RequestGuildMembers command serialization`() {
        val guildId = "1337"
        val query = "test"
        val limit = 1337

        val request = Json.encodeToString(Command.Companion, RequestGuildMembers(listOf(guildId), query, limit))

        val json = Json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.RequestGuildMembers.code)
            put("d", buildJsonObject {
                put("guild_id", buildJsonArray {
                    add(guildId)
                })
                put("query", query)
                put("limit", limit)
                put("presences", null as Int?)
                put("user_ids", null as Int?)
            })
        })

        assertEquals(json, request)
    }


    @Test
    fun `UpdateVoiceState command serialization`() {
        val guildId = "1337"
        val channelId = "420"
        val selfMute = true
        val selfDeaf = false

        val status = Json.encodeToString(Command.Companion, UpdateVoiceStatus(guildId, channelId, selfMute, selfDeaf))

        val json = Json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.VoiceStateUpdate.code)
            put("d", buildJsonObject {
                put("guild_id", guildId)
                put("channel_id", channelId)
                put("self_mute", selfMute)
                put("self_deaf", selfDeaf)
            })
        })

        assertEquals(json, status)
    }


    @Test
    fun `UpdateState command serialization`() {
        val since = 1242518400L
        val game = null
        val status = Status.Online
        val afk = false

        val updateStatus = Json.encodeToString(Command.Companion, UpdateStatus(since, game, status, afk))

        val json = Json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.StatusUpdate.code)
            put("d", buildJsonObject {
                put("since", since)
                put("game", null as String?)
                put("status", status.name.toLowerCase())
                put("afk", afk)
            })
        })

        assertEquals(json, updateStatus)
    }


    @OptIn(PrivilegedIntent::class)
    @Test
    fun `Identify command serialization`() {
        val token = "test"
        val properties = IdentifyProperties("os", "browser", "device")
        val compress = false
        val largeThreshold = 1337
        val shard = DiscordShard(0, 1)
        val presence = null

        val identify = Json.encodeToString(Command.Companion, Identify(token, properties, compress, largeThreshold, shard, presence, Intents.all))

        val json = Json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.Identify.code)
            put("d", buildJsonObject {
                put("token", token)
                put("properties", buildJsonObject {
                    put("\$os", "os")
                    put("\$browser", "browser")
                    put("\$device", "device")
                })
                put("compress", compress)
                put("large_threshold", largeThreshold)
                put("shard", buildJsonArray {
                    add(JsonPrimitive(0))
                    add(JsonPrimitive(1))
                })
                put("presence", null as String?)
                put("intents", Intents.all.code)
            })
        })

        assertEquals(json, identify)
    }
}