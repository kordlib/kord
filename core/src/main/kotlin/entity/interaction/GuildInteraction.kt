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

    /**
     * [Permissions] the [interaction invoker][user] has within the [channel][GuildInteractionBehavior.channel] the
     * interaction was sent from.
     */
    public val permissions: Permissions get() = data.permissions.value!!

    /** The invoker of the interaction as a [Member]. */
    override val user: Member get() = Member(data.member.value!!, data.user.value!!, kord)

    /**
     * [Permissions] the [application][applicationId] has within the [channel][GuildInteractionBehavior.channel] the
     * interaction was sent from.
     */
    public val appPermissions: Permissions get() = data.appPermissions.value!!

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildInteraction
}
