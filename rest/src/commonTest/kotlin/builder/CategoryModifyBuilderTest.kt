package dev.kord.rest.builder

import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.channel.CategoryModifyBuilder
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

class CategoryModifyBuilderTest {

    @Test
    @JsName("test1")
    fun `builder does not create empty overwrites by default`() {
        val builder = CategoryModifyBuilder()

        val request = builder.toRequest()

        assertEquals(Optional.Missing(), request.permissionOverwrites)
    }

}
