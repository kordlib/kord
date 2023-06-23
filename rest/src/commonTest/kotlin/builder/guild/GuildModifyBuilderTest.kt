package dev.kord.rest.builder.guild

import dev.kord.common.Locale
import dev.kord.common.entity.optional.Optional
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GuildModifyBuilderTest {

    @Test
    @JsName("test1")
    fun `builder omits non -region and -language`() {
        val builder = GuildModifyBuilder()
        builder.preferredLocale = Locale.ENGLISH_GREAT_BRITAIN

        val request = builder.toRequest()
        assertEquals(Optional.Value("en-GB"), request.preferredLocale)
    }

}
