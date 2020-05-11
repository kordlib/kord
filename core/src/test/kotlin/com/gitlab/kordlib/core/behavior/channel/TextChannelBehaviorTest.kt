package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.core.cache.data.ChannelData
import equality.GuildChannelEqualityTest
import io.mockk.mockk

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class TextChannelBehaviorTest : GuildChannelEqualityTest<TextChannelBehavior> by GuildChannelEqualityTest({ id, guildId ->
    TextChannelBehavior(id = id, guildId = guildId, kord = mockk())
})