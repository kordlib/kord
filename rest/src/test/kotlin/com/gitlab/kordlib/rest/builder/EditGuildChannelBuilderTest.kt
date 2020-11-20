package com.gitlab.kordlib.rest.builder

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.rest.builder.channel.NewsChannelModifyBuilder
import com.gitlab.kordlib.rest.builder.channel.StoreChannelModifyBuilder
import com.gitlab.kordlib.rest.builder.channel.TextChannelModifyBuilder
import com.gitlab.kordlib.rest.builder.channel.VoiceChannelModifyBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EditGuildChannelBuilderTest {
    /*
     2020-11-16 Kotlin 1.4.20
     You might think these explicit generic types to be unneeded, but the kotlin compiler
     won't be able to generate valid bytecode without them. Remove with care.
     */

    @Test
    fun `text builder does not create empty overwrites by default`() {
        val builder = TextChannelModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(Optional.Missing<Set<Overwrite>>(), request.permissionOverwrites)
    }

    @Test
    fun `voice builder does not create empty overwrites by default`() {
        val builder = VoiceChannelModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(Optional.Missing<Set<Overwrite>>(), request.permissionOverwrites)
    }

    @Test
    fun `news builder does not create empty overwrites by default`() {
        val builder = NewsChannelModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(Optional.Missing<Set<Overwrite>>(), request.permissionOverwrites)
    }

    @Test
    fun `store builder does not create empty overwrites by default`() {
        val builder = StoreChannelModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(Optional.Missing<Set<Overwrite>>(), request.permissionOverwrites)
    }

}