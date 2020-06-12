package com.gitlab.kordlib.core.event

import com.gitlab.kordlib.common.entity.DiscordPresenceUser
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Presence
import com.gitlab.kordlib.core.entity.User

class PresenceUpdateEvent internal constructor(
        val oldUser: User?,
        val user: DiscordPresenceUser,
        val guildId: Snowflake,
        val old: Presence?,
        val presence: Presence
) : Event {
    override val kord: Kord get() = presence.kord

    /**
     * The behavior of the member whose presence was updated.
     */
    val member: MemberBehavior get() = MemberBehavior(id = Snowflake(user.id), guildId = guildId, kord = kord)

    /**
     * The behavior of the guild in which the presence was updated.
     */
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the user whose presence was updated.
     */
    suspend fun getUser(): User = kord.getUser(Snowflake(user.id))!!

    /**
     * Requests to get the member whose presence was updated.
     */
    suspend fun getMember(): Member = kord.getMember(guildId = guildId, userId = Snowflake(user.id))!!

    /**
     * Requests to get the guild in which the presence was updated.
     */
    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!

}
