package dev.kord.gateway.json

import dev.kord.common.entity.DiscordBotActivity
import dev.kord.common.entity.DiscordShard
import dev.kord.common.entity.PresenceStatus
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.optional
import dev.kord.common.entity.optional.optionalInt
import dev.kord.gateway.*
import dev.kord.test.IgnoreOnSimulatorPlatforms
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

private val json = Json { encodeDefaults = false }

@IgnoreOnSimulatorPlatforms
class CommandTest {
    @Test
    @JsName("test1")
    fun `Resume command serialization`() {
        val token = "token"
        val sessionId = "session"
        val sequence = 1337

        val resume = json.encodeToString(Command.SerializationStrategy, Resume(token, sessionId, sequence))

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
    @JsName("test2")
    fun `Heartbeat command serialization`() {
        val interval = 1337

        val heartbeat = json.encodeToString(Command.SerializationStrategy, Command.Heartbeat(interval))

        val json = json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.Heartbeat.code)
            put("d", interval)
        })

        assertEquals(json, heartbeat)
    }


    @Test
    @JsName("test3")
    @OptIn(PrivilegedIntent::class)
    fun `RequestGuildMembers command serialization`() {
        val guildId = "1337"
        val query = "test"
        val limit = 1337

        val request = json.encodeToString(
            Command.SerializationStrategy,
            RequestGuildMembers(Snowflake(guildId), query.optional(), OptionalInt.Value(limit))
        )

        val json = json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.RequestGuildMembers.code)
            put("d", buildJsonObject {
                put("guild_id", guildId.toLong())
                put("query", query)
                put("limit", limit)
            })
        })

        assertEquals(json, request)
    }


    @Test
    @JsName("test4")
    fun `UpdateVoiceState command serialization`() {
        val guildId = "1337"
        val channelId = "420"
        val selfMute = true
        val selfDeaf = false

        val status = json.encodeToString(
            Command.SerializationStrategy,
            UpdateVoiceStatus(Snowflake(guildId), Snowflake(channelId), selfMute, selfDeaf)
        )

        val json = json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.VoiceStateUpdate.code)
            put("d", buildJsonObject {
                put("guild_id", guildId.toLong())
                put("channel_id", channelId.toLong())
                put("self_mute", selfMute)
                put("self_deaf", selfDeaf)
            })
        })

        assertEquals(json, status)
    }


    @Test
    @JsName("test5")
    fun `UpdateState command serialization`() {
        val since = 1242518400L
        val activities = listOf<DiscordBotActivity>()
        val status = PresenceStatus.Online
        val afk = false

        val updateStatus = json.encodeToString(
            Command.SerializationStrategy,
            UpdateStatus(Instant.fromEpochMilliseconds(since), activities, status, afk),
        )

        val json = json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.StatusUpdate.code)
            put("d", buildJsonObject {
                put("since", since)
                put("activities", JsonArray(emptyList()))
                put("status", status.value.lowercase())
                put("afk", afk)
            })
        })

        assertEquals(json, updateStatus)
    }


    @OptIn(PrivilegedIntent::class)
    @Test
    @JsName("test6")
    fun `Identify command serialization`() {
        val token = "test"
        val properties = IdentifyProperties("os", "browser", "device")
        val compress = false
        val largeThreshold = 1337
        val shard = DiscordShard(0, 1)
        val presence: DiscordPresence? = null

        val identify = json.encodeToString(
            Command.SerializationStrategy,
            Identify(
                token,
                properties,
                compress.optional(),
                largeThreshold.optionalInt(),
                shard.optional(),
                presence.optional().coerceToMissing(),
                Intents.ALL
            )
        )

        val json = json.encodeToString(JsonObject.serializer(), buildJsonObject {
            put("op", OpCode.Identify.code)
            put("d", buildJsonObject {
                put("token", token)
                put("properties", buildJsonObject {
                    put("os", "os")
                    put("browser", "browser")
                    put("device", "device")
                })
                put("compress", compress)
                put("large_threshold", largeThreshold)
                put("shard", buildJsonArray {
                    add(JsonPrimitive(0))
                    add(JsonPrimitive(1))
                })
                put("intents", Intents.ALL.code.value)
            })
        })

        assertEquals(json, identify)
    }
}
