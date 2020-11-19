package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.optional.optionalSnowflake
import com.gitlab.kordlib.core.cache.data.ChannelData
import equality.GuildChannelEqualityTest
import mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class VoiceChannelTest : GuildChannelEqualityTest<VoiceChannel> by GuildChannelEqualityTest({ id, guildId ->
    val kord = mockKord()
    VoiceChannel(ChannelData(id, guildId = guildId.optionalSnowflake(), type = ChannelType.GuildNews), kord)
})