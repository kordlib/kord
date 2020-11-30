package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.core.event.Event

class MemberLeaveEvent(val user: User, val guildId: Snowflake, override val shard: Int) : Event {

    override val kord: Kord get() = user.kord

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild(): Guild = guild.asGuild()

    suspend fun getGuildOrNull(): Guild? = guild.asGuildOrNull()

    override fun toString(): String {
        return "MemberLeaveEvent(user=$user, guildId=$guildId, shard=$shard)"
    }

}
