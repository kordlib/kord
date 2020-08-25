package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.core.cache.data.ChannelData
import equality.GuildChannelEqualityTest
import mockKord

@OptIn(KordUnstableApi::class)
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class NewsChannelTest : GuildChannelEqualityTest<NewsChannel> by GuildChannelEqualityTest({ id, guildId ->
    val kord = mockKord()
    NewsChannel(ChannelData(id.longValue, guildId = guildId.longValue, type = ChannelType.GuildNews), kord)
})