package dev.kord.core.behavior.channel

import equality.GuildChannelEqualityTest
import mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class VoiceChannelBehaviorTest : GuildChannelEqualityTest<VoiceChannelBehavior> by GuildChannelEqualityTest({ id, guildId ->
    val kord = mockKord()
    VoiceChannelBehavior(id = id, guildId = guildId, kord = kord)
})