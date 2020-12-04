package dev.kord.rest.builder

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.member.MemberModifyBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MemberModifyBuilderTest {

    @Test
    fun `builder does not create empty roles by default`() {
        val builder = MemberModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(Optional.Missing<Iterable<Snowflake>>(), request.roles)
    }

}