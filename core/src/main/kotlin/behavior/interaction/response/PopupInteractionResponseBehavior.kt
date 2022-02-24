package dev.kord.core.behavior.interaction.response

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.ActionInteractionBehavior
import dev.kord.core.entity.Strategizable
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public  interface PopupInteractionResponseBehavior : InteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PopupInteractionResponseBehavior {
        return PopupInteractionResponseBehavior(applicationId, token, kord, strategy.supply(kord))
    }
}


public fun PopupInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): PopupInteractionResponseBehavior =
    object : PopupInteractionResponseBehavior {
        override val applicationId: Snowflake = applicationId

        override val token: String = token

        override val kord: Kord = kord

        override val supplier: EntitySupplier = supplier
    }
