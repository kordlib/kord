package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.core.cache.data.GuildData
import com.gitlab.kordlib.core.cache.data.UserData
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class GuildBehaviorTest: EntityEqualityTest<GuildBehavior> by EntityEqualityTest({
    GuildBehavior(it, mockk())
})