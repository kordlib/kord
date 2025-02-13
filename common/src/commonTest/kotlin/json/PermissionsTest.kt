package dev.kord.common.json

import dev.kord.common.entity.DiscordRole
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.test.IgnoreOnSimulatorPlatforms
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

@IgnoreOnSimulatorPlatforms
class PermissionsTest {

    @Test
    @JsName("test1")
    fun `adding permissions together does not swallow the universe`() {
        Permission.entries.fold(Permissions()) { acc, permission ->
            acc + permission
        }
    }

    @Test
    @JsName("test3")
    fun `permissions serialization test`() {
        val expected = buildJsonObject {
            put("id", "12323232")
            put("name", "Korduers")
            put("color", 0)
            put("hoist", true)
            put("position", 0)
            put("permissions", "123456789876543000000000000")
            put("managed", false)
            put("mentionable", false)
            put("flags", 0)
        }
        val actual = Json.decodeFromJsonElement(DiscordRole.serializer(), expected)
        assertEquals(
            "123456789876543000000000000", actual.permissions.code.value,
            "1234567898765430000000000 was expected but ${actual.permissions.code.value} was found"
        )

    }

}
