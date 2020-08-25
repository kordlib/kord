package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.core.cache.data.ChannelData
import equality.ChannelEqualityTest
import mockKord

@OptIn(KordUnstableApi::class)
internal class DmChannelTest: ChannelEqualityTest<DmChannel> by ChannelEqualityTest ({ id ->
    val kord = mockKord()
    DmChannel(ChannelData(id.longValue, type = ChannelType.DM), kord)
})