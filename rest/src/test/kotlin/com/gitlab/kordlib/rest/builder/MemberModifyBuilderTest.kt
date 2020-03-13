package com.gitlab.kordlib.rest.builder

import com.gitlab.kordlib.rest.builder.member.MemberModifyBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MemberModifyBuilderTest {

    @Test
    fun `builder does not create empty roles by default`() {
        val builder = MemberModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(null, request.roles)
    }

}