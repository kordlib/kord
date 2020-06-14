package com.gitlab.kordlib.core.event

import com.gitlab.kordlib.common.entity.DiscordPresenceUser
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.exception.EntityNotFoundException
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
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the user isn't present.
     */
    suspend fun getUser(): User = supplier.getUser(Snowflake(user.id))

    /**
     * Requests to get the user whose presence was updated, returns null if the user isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun getUserOrNull(): User? = supplier.getUserOrNull(Snowflake(user.id))

    /**
     * Requests to get the member whose presence was updated.
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the member isn't present.
     */
    suspend fun getMember(): Member = supplier.getMember(guildId = guildId, userId = Snowflake(user.id))

    /**
     * Requests to get the member whose presence was updated, returns null if the member isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun getMemberOrNull(): Member? = supplier.getMemberOrNull(guildId = guildId, userId = Snowflake(user.id))

    /**
     * Requests to get the guild in which the presence was updated.
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the guild isn't present.
     */
    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild in which the presence was updated, returns null if the guild isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PresenceUpdateEvent =
            PresenceUpdateEvent(oldUser, user, guildId, old, presence, shard, strategy.supply(kord))
}
