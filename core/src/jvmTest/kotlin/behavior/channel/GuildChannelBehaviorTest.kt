package dev.kord.core.behavior.channel

import dev.kord.core.equality.GuildChannelEqualityTest
import dev.kord.core.mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class GuildChannelBehaviorTest : GuildChannelEqualityTest<TopGuildChannelBehavior> by GuildChannelEqualityTest({ id, guildId ->
    val kord = mockKord()
    TopGuildChannelBehavior(id = id, guildId = guildId, kord = kord)
})
