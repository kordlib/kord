package dev.kord.core.event.user

import dev.kord.common.entity.DiscordPresenceUser
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.entity.*
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

class PresenceUpdateEvent(
    val oldUser: User?,
    val user: DiscordPresenceUser,
    override val guildId: Snowflake,
    val old: Presence?,
    val presence: Presence,
    override val shard: Int,
    override val supplier: EntitySupplier = presence.kord.defaultSupplier
) : Event, Strategizable {
    override val kord: Kord get() = presence.kord

    /**
     * The behavior of the member whose presence was updated.
     */
    val member: MemberBehavior get() = MemberBehavior(id = user.id, guildId = guildId, kord = kord)

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
    suspend fun getUser(): User = supplier.getUser(user.id)

    /**
     * Requests to get the user whose presence was updated, returns null if the user isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun getUserOrNull(): User? = supplier.getUserOrNull(user.id)

    /**
     * Requests to get the member whose presence was updated.
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the member isn't present.
     */
    suspend fun getMember(): Member = supplier.getMember(guildId = guildId, userId = user.id)

    /**
     * Requests to get the member whose presence was updated, returns null if the member isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun getMemberOrNull(): Member? = supplier.getMemberOrNull(guildId = guildId, userId = user.id)

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

    override fun toString(): String {
        return "PresenceUpdateEvent(oldUser=$oldUser, user=$user, guildId=$guildId, old=$old, presence=$presence, shard=$shard, supplier=$supplier)"
    }
}
