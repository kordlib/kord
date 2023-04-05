package dev.kord.core.cache.data

import dev.kord.common.entity.ComponentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentDataTest {

    @Test
    @JsName("test1")
    fun `polymorphic ComponentData can be serialized`() {
        val type = ComponentType.ActionRow
        val data: ComponentData = ChatComponentData(type)
        assertEquals(
            expected = """{"_type":"dev.kord.core.cache.data.ChatComponentData","type":${type.value}}""",
            actual = Json.encodeToString(data),
        )
    }
}
