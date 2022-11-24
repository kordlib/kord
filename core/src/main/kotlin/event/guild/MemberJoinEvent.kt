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
 * The event dispatched when a new user joins a guild.
 *
 * See [Guild Member Add](https://discord.com/developers/docs/topics/gateway-events#guild-member-add)
 */
public class MemberJoinEvent(
    public val member: Member,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = member.kord.defaultSupplier,
) : Event, Strategizable {

    override val kord: Kord get() = member.kord

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

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MemberJoinEvent =
        MemberJoinEvent(member, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "MemberJoinEvent(member=$member, shard=$shard, supplier=$supplier)"
    }

}
