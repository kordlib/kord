@file:Suppress("EXPERIMENTAL_API_USAGE")

package json

import dev.kord.common.entity.DiscordBotActivity
import dev.kord.common.entity.DiscordShard
import dev.kord.common.entity.PresenceStatus
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.optional
import dev.kord.common.entity.optional.optionalInt
import dev.kord.gateway.*
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

private val json = Json { encodeDefaults = false }

class CommandTest {
    @Test
    fun `Resume command serialization`() {
        val token = "token"
        val sessionId = "session"
        val sequence = 1337

        val resume = json.encodeToString(Command.Companion, Resume(token, sessionId, sequence))

        val json = json.encodeToString(JsonObject.serializer(), buildJsonObject {
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

        val heartbeat = json.encodeToString(Command.Companion, Command.Heartbeat(interval))

        val json = json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.Heartbeat.code)
            put("d", interval)
        })

        assertEquals(json, heartbeat)
    }


    @Test
    @OptIn(PrivilegedIntent::class)
    fun `RequestGuildMembers command serialization`() {
        val guildId = "1337"
        val query = "test"
        val limit = 1337

        val request = json.encodeToString(
            Command.Companion,
            RequestGuildMembers(Snowflake(guildId), query.optional(), OptionalInt.Value(limit))
        )

        val json = json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.RequestGuildMembers.code)
            put("d", buildJsonObject {
                put("guild_id", guildId)
                put("query", query)
                put("limit", limit)
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

        val status = json.encodeToString(
            Command.Companion,
            UpdateVoiceStatus(Snowflake(guildId), Snowflake(channelId), selfMute, selfDeaf)
        )

        val json = json.encodeToString(JsonObject.serializer(), buildJsonObject {
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
        val game = emptyList<DiscordBotActivity>()
        val status = PresenceStatus.Online
        val afk = false

        val updateStatus = json.encodeToString(Command.Companion, UpdateStatus(since, game, status, afk))

        val json = json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.StatusUpdate.code)
            put("d", buildJsonObject {
                put("since", since)
                put("activities", JsonArray(emptyList()))
                put("status", status.value.lowercase(Locale.getDefault()))
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
        val presence: DiscordPresence? = null

        val identify = json.encodeToString(
            Command.Companion,
            Identify(
                token,
                properties,
                compress.optional(),
                largeThreshold.optionalInt(),
                shard.optional(),
                presence.optional().coerceToMissing(),
                Intents.all
            )
        )

        val json = json.encodeToString(JsonObject.serializer(), buildJsonObject {
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
                put("intents", Intents.all.code.value)
            })
        })

        assertEquals(json, identify)
    }
}
