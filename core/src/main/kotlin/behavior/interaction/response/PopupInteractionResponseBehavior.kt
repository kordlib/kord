package dev.kord.core.behavior.interaction.response

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public interface PopupInteractionResponseBehavior : FollowupableInteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PopupInteractionResponseBehavior =
        PopupInteractionResponseBehavior(applicationId, token, kord, strategy.supply(kord))
}

public fun PopupInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): PopupInteractionResponseBehavior = object : PopupInteractionResponseBehavior {
    override val applicationId: Snowflake = applicationId
    override val token: String = token
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}
