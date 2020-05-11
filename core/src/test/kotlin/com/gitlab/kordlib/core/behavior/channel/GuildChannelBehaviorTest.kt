package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.entity.channel.GuildChannel
import equality.GuildChannelEqualityTest
import io.mockk.mockk

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class GuildChannelBehaviorTest : GuildChannelEqualityTest<GuildChannelBehavior> by GuildChannelEqualityTest({ id, guildId ->
    GuildChannelBehavior(id = id, guildId = guildId, kord = mockk())
})