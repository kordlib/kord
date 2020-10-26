package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.common.entity.Snowflake
import equality.GuildEntityEqualityTest
import io.mockk.mockk
import mockKord
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class RoleBehaviorTest : GuildEntityEqualityTest<RoleBehavior> by GuildEntityEqualityTest({ id, guildId ->
    val kord = mockKord()
    RoleBehavior(guildId = guildId, id = id, kord = kord)
}) {

    @Test
    fun `everyone role mention is properly formatted`(){
        val kord = mockKord()

        val id = Snowflake(1337)
        val behavior = RoleBehavior(id, id, kord)

        assertEquals("@everyone", behavior.mention)
    }

}