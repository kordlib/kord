package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.core.cache.data.ChannelData
import equality.ChannelEqualityTest
import equality.GuildEntityEqualityTest
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class DmChannelTest: ChannelEqualityTest<DmChannel> by ChannelEqualityTest ({ id ->
    DmChannel(ChannelData(id.longValue, type = ChannelType.DM), mockk())
})