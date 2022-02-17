package dev.kord.core.entity.interaction

import dev.kord.core.behavior.interaction.GlobalInteractionBehavior
import dev.kord.core.entity.User

public sealed interface GlobalInteraction : GlobalInteractionBehavior, Interaction {
    public override val user: User get() = User(data.user.value!!, kord)
}
