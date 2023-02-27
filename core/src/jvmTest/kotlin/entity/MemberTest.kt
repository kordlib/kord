package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.UserData
import equality.BehaviorEqualityTest
import equality.GuildEntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class MemberTest : GuildEntityEqualityTest<Member> by GuildEntityEqualityTest({ id, guildId ->
    val kord = mockKord()
    val memberData = mockk<MemberData>()
    every { memberData.userId } returns id
    every { memberData.guildId } returns guildId

    val userData = mockk<UserData>()
    every { userData.id } returns id

    Member(memberData, userData, kord)
}), BehaviorEqualityTest<Member> {
    override fun Member.behavior(): KordEntity = MemberBehavior(guildId = guildId, id = id, kord = kord)

    @Test
    fun `members equal users with the same ID`() {
        val kord = mockKord()
        val memberData = mockk<MemberData>()
        every { memberData.userId } returns Snowflake(0u)
        every { memberData.guildId } returns Snowflake(1u)

        val userData = mockk<UserData>()
        every { userData.id } returns Snowflake(0u)
        val member = Member(memberData, userData, kord)
        val user = User(userData, kord)

        assertEquals(member, user)
        assertEquals(user, member)
    }


}
