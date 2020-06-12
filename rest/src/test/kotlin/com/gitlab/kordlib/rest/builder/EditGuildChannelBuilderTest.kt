package com.gitlab.kordlib.rest.builder

import com.gitlab.kordlib.rest.builder.channel.NewsChannelModifyBuilder
import com.gitlab.kordlib.rest.builder.channel.StoreChannelModifyBuilder
import com.gitlab.kordlib.rest.builder.channel.TextChannelModifyBuilder
import com.gitlab.kordlib.rest.builder.channel.VoiceChannelModifyBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EditGuildChannelBuilderTest {

    @Test
    fun `text builder does not create empty overwrites by default`() {
        val builder = TextChannelModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(null, request.permissionOverwrites)
    }

    @Test
    fun `voice builder does not create empty overwrites by default`() {
        val builder = VoiceChannelModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(null, request.permissionOverwrites)
    }

    @Test
    fun `news builder does not create empty overwrites by default`() {
        val builder = NewsChannelModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(null, request.permissionOverwrites)
    }

    @Test
    fun `store builder does not create empty overwrites by default`() {
        val builder = StoreChannelModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(null, request.permissionOverwrites)
    }

}