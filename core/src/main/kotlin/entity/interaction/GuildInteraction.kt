package dev.kord.core.entity.interaction

import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member

public sealed interface GuildInteraction : Interaction {
    public val guildId: Snowflake get() = data.guildId.value!!
    /**
     * Overridden permissions of the interaction invoker in the channel.
     */
    public val permission: Permissions get() = data.permissions.value!!

    /**
     * The invoker of the command as [Member].
     */
    override val user: Member get() = Member(data.member.value!!, data.user.value!!, kord)


    override val channel: GuildMessageChannelBehavior
        get() = GuildMessageChannelBehavior(guildId, channelId, kord)


    public suspend fun getGuild(): Guild {
        return supplier.getGuild(guildId)
    }
    public suspend fun getGuildOrNull(): Guild? {
        return supplier.getGuildOrNull(guildId)
    }
}