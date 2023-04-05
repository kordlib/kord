package dev.kord.rest.builder

import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.channel.NewsChannelModifyBuilder
import dev.kord.rest.builder.channel.TextChannelModifyBuilder
import dev.kord.rest.builder.channel.VoiceChannelModifyBuilder
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

class EditGuildChannelBuilderTest {
    @Test
    @JsName("test1")
    fun `text builder does not create empty overwrites by default`() {
        val builder = TextChannelModifyBuilder()

        val request = builder.toRequest()

        assertEquals(Optional.Missing(), request.permissionOverwrites)
    }

    @Test
    @JsName("test2")
    fun `voice builder does not create empty overwrites by default`() {
        val builder = VoiceChannelModifyBuilder()

        val request = builder.toRequest()

        assertEquals(Optional.Missing(), request.permissionOverwrites)
    }

    @Test
    @JsName("test3")
    fun `news builder does not create empty overwrites by default`() {
        val builder = NewsChannelModifyBuilder()

        val request = builder.toRequest()

        assertEquals(Optional.Missing(), request.permissionOverwrites)
    }
}
