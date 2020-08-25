package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.core.cache.data.ChannelData
import equality.GuildChannelEqualityTest
import mockKord

@OptIn(KordUnstableApi::class)
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class CategoryTest : GuildChannelEqualityTest<Category> by GuildChannelEqualityTest ({ id, guildId ->
    val kord = mockKord()
    Category(ChannelData(id.longValue, guildId = guildId.longValue, type = ChannelType.GuildCategory), kord)
}) {

}