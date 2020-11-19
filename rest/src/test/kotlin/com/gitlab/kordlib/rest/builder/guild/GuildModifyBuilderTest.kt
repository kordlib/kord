package com.gitlab.kordlib.rest.builder.guild

import com.gitlab.kordlib.common.entity.optional.Optional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class GuildModifyBuilderTest {

    @Test
    fun `builder omits non -region and -language`() {
        val builder = GuildModifyBuilder()
        builder.preferredLocale = Locale.Builder().setLanguage("en").setRegion("gb").addUnicodeLocaleAttribute("short").build()

        val request = builder.toRequest()
        assertEquals(Optional.Value("en-GB"), request.preferredLocale)
    }

}
