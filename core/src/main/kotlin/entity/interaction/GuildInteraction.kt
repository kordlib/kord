package dev.kord.core.entity.interaction

import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.GuildInteractionBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.supplier.EntitySupplyStrategy

/** An [Interaction] that took place in the context of a [Guild]. */
public sealed interface GuildInteraction : Interaction, GuildInteractionBehavior {

    override val guildId: Snowflake get() = data.guildId.value!!

    /** Overridden permissions of the interaction [invoker][user] in the [channel][GuildInteractionBehavior.channel]. */
    public val permissions: Permissions get() = data.permissions.value!!

    /** The invoker of the interaction as a [Member]. */
    override val user: Member get() = Member(data.member.value!!, data.user.value!!, kord)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildInteraction
}
