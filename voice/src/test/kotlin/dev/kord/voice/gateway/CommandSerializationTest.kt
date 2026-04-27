package dev.kord.voice.gateway

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.*
import org.junit.jupiter.api.Nested

class CommandSerializationTest {

    private val json = Json { encodeDefaults = true }

    private fun serializeCommand(command: Command): JsonObject {
        val jsonString = json.encodeToString(Command.SerializationStrategy, command)
        return json.parseToJsonElement(jsonString).jsonObject
    }

    @Nested
    inner class IdentifyCommand {
        @Test
        fun `Identify serializes max_dave_protocol_version field`() {
            val identify = Identify(
                serverId = Snowflake(123456789L),
                userId = Snowflake(987654321L),
                sessionId = "test-session",
                token = "test-token",
                maxDaveProtocolVersion = 1
            )

            val result = serializeCommand(identify)
            val data = result["d"]!!.jsonObject
            assertEquals("1", data["max_dave_protocol_version"]!!.jsonPrimitive.content)
        }

        @Test
        fun `Identify defaults max_dave_protocol_version to 0`() {
            val identify = Identify(
                serverId = Snowflake(123456789L),
                userId = Snowflake(987654321L),
                sessionId = "test-session",
                token = "test-token"
            )

            val result = serializeCommand(identify)
            val data = result["d"]!!.jsonObject
            assertEquals("0", data["max_dave_protocol_version"]!!.jsonPrimitive.content)
        }

        @Test
        fun `Identify serializes all required fields`() {
            val identify = Identify(
                serverId = Snowflake(111L),
                userId = Snowflake(222L),
                sessionId = "sess",
                token = "tok",
                maxDaveProtocolVersion = 2
            )

            val result = serializeCommand(identify)
            val data = result["d"]!!.jsonObject
            assertEquals("111", data["server_id"]!!.jsonPrimitive.content)
            assertEquals("222", data["user_id"]!!.jsonPrimitive.content)
            assertEquals("sess", data["session_id"]!!.jsonPrimitive.content)
            assertEquals("tok", data["token"]!!.jsonPrimitive.content)
            assertEquals("2", data["max_dave_protocol_version"]!!.jsonPrimitive.content)
        }

        @Test
        fun `Identify uses opcode 0`() {
            val identify = Identify(
                serverId = Snowflake(1L),
                userId = Snowflake(2L),
                sessionId = "s",
                token = "t"
            )

            val result = serializeCommand(identify)
            assertEquals("0", result["op"]!!.jsonPrimitive.content)
        }
    }

    @Nested
    inner class ResumeCommand {
        @Test
        fun `Resume serializes max_dave_protocol_version field`() {
            val resume = Resume(
                serverId = Snowflake(123456789L),
                sessionId = "test-session",
                token = "test-token",
                maxDaveProtocolVersion = 1
            )

            val result = serializeCommand(resume)
            val data = result["d"]!!.jsonObject
            assertEquals("1", data["max_dave_protocol_version"]!!.jsonPrimitive.content)
        }

        @Test
        fun `Resume defaults max_dave_protocol_version to 0`() {
            val resume = Resume(
                serverId = Snowflake(123456789L),
                sessionId = "test-session",
                token = "test-token"
            )

            val result = serializeCommand(resume)
            val data = result["d"]!!.jsonObject
            assertEquals("0", data["max_dave_protocol_version"]!!.jsonPrimitive.content)
        }

        @Test
        fun `Resume uses opcode 7`() {
            val resume = Resume(
                serverId = Snowflake(1L),
                sessionId = "s",
                token = "t"
            )

            val result = serializeCommand(resume)
            assertEquals("7", result["op"]!!.jsonPrimitive.content)
        }
    }

    @Nested
    inner class DaveCommands {
        @Test
        fun `DaveProtocolReadyForTransition serializes correctly`() {
            val cmd = DaveProtocolReadyForTransition(transitionId = 42)

            val result = serializeCommand(cmd)
            val data = result["d"]!!.jsonObject
            assertEquals("42", data["transition_id"]!!.jsonPrimitive.content)
        }

        @Test
        fun `DaveProtocolReadyForTransition uses opcode 23`() {
            val cmd = DaveProtocolReadyForTransition(transitionId = 1)

            val result = serializeCommand(cmd)
            assertEquals("23", result["op"]!!.jsonPrimitive.content)
        }

        @Test
        fun `DaveMlsInvalidCommitWelcome serializes correctly`() {
            val cmd = DaveMlsInvalidCommitWelcome(transitionId = 99)

            val result = serializeCommand(cmd)
            val data = result["d"]!!.jsonObject
            assertEquals("99", data["transition_id"]!!.jsonPrimitive.content)
        }

        @Test
        fun `DaveMlsInvalidCommitWelcome uses opcode 31`() {
            val cmd = DaveMlsInvalidCommitWelcome(transitionId = 1)

            val result = serializeCommand(cmd)
            assertEquals("31", result["op"]!!.jsonPrimitive.content)
        }
    }
}
