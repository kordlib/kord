package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import kotlin.coroutines.CoroutineContext

public class MemberLeaveEvent(
    public val user: User,
    public val guildId: Snowflake,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = user.kord.coroutineContext,
) : Event {

    override val kord: Kord get() = user.kord

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuild(): Guild = guild.asGuild()

    public suspend fun getGuildOrNull(): Guild? = guild.asGuildOrNull()

    override fun toString(): String {
        return "MemberLeaveEvent(user=$user, guildId=$guildId, shard=$shard)"
    }

}
