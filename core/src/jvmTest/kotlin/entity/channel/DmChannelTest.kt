package dev.kord.core.entity.channel

import dev.kord.common.entity.ChannelType
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.equality.ChannelEqualityTest
import dev.kord.core.mockKord

internal class DmChannelTest: ChannelEqualityTest<DmChannel> by ChannelEqualityTest ({ id ->
    val kord = mockKord()
    DmChannel(ChannelData(id, type = ChannelType.DM), kord)
})
