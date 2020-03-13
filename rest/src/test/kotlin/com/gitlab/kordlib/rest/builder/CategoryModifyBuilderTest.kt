package com.gitlab.kordlib.rest.builder

import com.gitlab.kordlib.rest.builder.channel.CategoryModifyBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CategoryModifyBuilderTest {

    @Test
    fun `builder does not create empty overwrites by default`() {
        val builder = CategoryModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(null, request.permissionOverwrites)
    }

}