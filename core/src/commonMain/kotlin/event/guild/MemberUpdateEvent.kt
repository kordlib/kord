package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The event dispatched when a guild member is updated. This event is also dispatched when the user object of a guild member changes.
 *
 * The [old][old] [Member] may be `null`, unless it has been stored in the cache.
 *
 * See [Guild Member Update](https://discord.com/developers/docs/topics/gateway-events#guild-member-update)
 */
public class MemberUpdateEvent(
    public val member: Member,
    public val old: Member?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {
    /**
     * The ID of the guild that triggered this event.
     */
    public val guildId: Snowflake get() = member.guildId

    /**
     * The [Guild] that triggered this event.
     */
    public val guild: GuildBehavior get() = member.guild

    /**
     * Requests to get the [Guild] that triggered the event.
     *
     * @throws [RequestException] if anything went wrong during the request
     * @throws [EntityNotFoundException] if the guild was not present
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the [Guild] that triggered the event, or `null` if the guild was not present
     *
     * @throws [RequestException] if anything went wrong during the request
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MemberUpdateEvent =
        MemberUpdateEvent(member, old, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "MemberUpdateEvent(member=$member, old=$old, kord=$kord, shard=$shard, supplier=$supplier)"
    }

}
