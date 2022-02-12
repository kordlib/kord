package dev.kord.core.entity.interaction

import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.behavior.interaction.GuildInteractionBehavior
import dev.kord.core.entity.Member

public sealed interface GuildInteraction : Interaction, GuildInteractionBehavior {
    public override val guildId: Snowflake get() = data.guildId.value!!
    /**
     * Overridden permissions of the interaction invoker in the channel.
     */
    public val permissions: Permissions get() = data.permissions.value!!

    /**
     * The invoker of the command as [Member].
     */
    override val user: Member get() = Member(data.member.value!!, data.user.value!!, kord)


    override val channel: GuildMessageChannelBehavior
        get() = GuildMessageChannelBehavior(guildId, channelId, kord)

}