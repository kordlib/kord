package dev.kord.core.entity.interaction

import dev.kord.core.behavior.interaction.GlobalInteractionBehavior
import dev.kord.core.entity.User
import dev.kord.core.supplier.EntitySupplyStrategy

/** An [Interaction] that took place in a global context (e.g. a DM). */
public sealed interface GlobalInteraction : Interaction, GlobalInteractionBehavior {

    override val user: User get() = User(data.user.value!!, kord)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalInteraction
}
