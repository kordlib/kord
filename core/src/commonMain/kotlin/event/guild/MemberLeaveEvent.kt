package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException

/**
 * The event dispatched when a user is removed from a guild, via either leave, kick or ban.
 *
 * The [old][old] [Member] may be `null` unless it has been stored in the cache.
 *
 * See [Guild Member Remove](https://discord.com/developers/docs/topics/gateway-events#guild-member-remove)
 */
public class MemberLeaveEvent(
    public val user: User,
    public val old: Member?,
    public val guildId: Snowflake,
    override val shard: Int,
    override val customContext: Any?,
) : Event {

    override val kord: Kord get() = user.kord

    /**
     * The [Guild] that triggered this event.
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the [Guild] that triggered the event.
     *
     * @throws [RequestException] if anything went wrong during the request
     * @throws [EntityNotFoundException] if the guild was not present
     */
    public suspend fun getGuild(): Guild = guild.asGuild()

    /**
     * Requests to get the [Guild] that triggered the event, or `null` if the guild was not present
     *
     * @throws [RequestException] if anything went wrong during the request
     */
    public suspend fun getGuildOrNull(): Guild? = guild.asGuildOrNull()

    override fun toString(): String {
        return "MemberLeaveEvent(user=$user, old=$old, guildId=$guildId, shard=$shard)"
    }

}
