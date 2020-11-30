package dev.kord.core.entity.channel

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.cache.data.ChannelData
import equality.GuildChannelEqualityTest
import mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class StoreChannelTest : GuildChannelEqualityTest<StoreChannel> by GuildChannelEqualityTest({ id, guildId ->
    val kord = mockKord()
    StoreChannel(ChannelData(id, guildId = guildId.optionalSnowflake(), type = ChannelType.GuildNews), kord)
})