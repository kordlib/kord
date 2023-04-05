package dev.kord.rest.builder

import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.member.MemberModifyBuilder
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

class MemberModifyBuilderTest {

    @Test
    @JsName("test1")
    fun `builder does not create empty roles by default`() {
        val builder = MemberModifyBuilder()

        val request = builder.toRequest()

        assertEquals(Optional.Missing(), request.roles)
    }

}
