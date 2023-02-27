package dev.kord.core.behavior.channel

import equality.GuildChannelEqualityTest
import mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class GuildMessageChannelBehaviorTest : GuildChannelEqualityTest<TopGuildMessageChannelBehavior> by GuildChannelEqualityTest({ id, guildId ->
    val kord = mockKord()
    TopGuildMessageChannelBehavior(id = id, guildId = guildId, kord = kord)
})