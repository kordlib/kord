package dev.kord.core.entity.channel

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.cache.data.ChannelData
import equality.GuildChannelEqualityTest
import mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class NewsChannelTest : GuildChannelEqualityTest<NewsChannel> by GuildChannelEqualityTest({ id, guildId ->
    val kord = mockKord()
    NewsChannel(ChannelData(id, guildId = guildId.optionalSnowflake(), type = ChannelType.GuildNews), kord)
})