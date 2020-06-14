package com.gitlab.kordlib.core.event

import com.gitlab.kordlib.common.entity.DiscordPresenceUser
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy

class PresenceUpdateEvent(
        val oldUser: User?,
        val user: DiscordPresenceUser,
        val guildId: Snowflake,
        val old: Presence?,
        val presence: Presence,
        override val shard: Int,
        override val supplier: EntitySupplier = presence.kord.defaultSupplier
) : Event, Strategizable {
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
    suspend fun getMember(): Member = supplier.getMember(guildId = guildId, userId = Snowflake(user.id))!!

    /**
     * Requests to get the guild in which the presence was updated.
     */
    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PresenceUpdateEvent =
            PresenceUpdateEvent(oldUser, user, guildId, old, presence, shard, strategy.supply(kord))
}
