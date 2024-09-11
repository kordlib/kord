package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Ban
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class BanAddEvent(
    public val user: User,
    public val guildId: Snowflake,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = user.kord.defaultSupplier,
) : Event, Strategizable {

    override val kord: Kord get() = user.kord

    /**
     * The [Guild] this event was triggered from
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the [Guild] this ban happened in.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the guild wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the [Guild] this ban happened in,
     * returns `null` if the guild isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to get the [Ban] entity this event represents.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the ban wasn't present.
     */
    public suspend fun getBan(): Ban = supplier.getGuildBan(guildId, user.id)

    /**
     * Requests to get the [Ban] entity this event represents.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the ban wasn't present.
     */
    public suspend fun getBanOrNull(): Ban? = supplier.getGuildBanOrNull(guildId, user.id)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): BanAddEvent =
        BanAddEvent(user, guildId, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "BanAddEvent(user=$user, guildId=$guildId, shard=$shard, supplier=$supplier)"
    }
}
