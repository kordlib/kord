package json

import dev.kord.common.DiscordBitSet
import dev.kord.common.entity.DiscordRole
import dev.kord.common.entity.Permissions
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Test

class PermissionsTest {
    @Test
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
        }
        val actual = Json.decodeFromJsonElement(DiscordRole.serializer(), expected)
        assert(actual.permissions.code.value == "123456789876543000000000000") {
            "1234567898765430000000000 was expected but ${actual.permissions.code.value} was found"
        }

    }

}