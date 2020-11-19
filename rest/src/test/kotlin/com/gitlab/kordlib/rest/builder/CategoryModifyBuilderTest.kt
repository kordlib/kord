package com.gitlab.kordlib.rest.builder

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.rest.builder.channel.CategoryModifyBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CategoryModifyBuilderTest {

    @Test
    fun `builder does not create empty overwrites by default`() {
        val builder = CategoryModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(Optional.Missing<Iterable<Overwrite>>(), request.permissionOverwrites)
    }

}