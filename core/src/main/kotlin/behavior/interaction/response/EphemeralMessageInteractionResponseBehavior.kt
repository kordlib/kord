package dev.kord.core.behavior.interaction.response

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public interface EphemeralMessageInteractionResponseBehavior :
    EphemeralInteractionResponseBehavior,
    MessageInteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralMessageInteractionResponseBehavior =
        EphemeralMessageInteractionResponseBehavior(applicationId, token, kord, strategy.supply(kord))
}

public fun EphemeralMessageInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): EphemeralMessageInteractionResponseBehavior = object : EphemeralMessageInteractionResponseBehavior {
    override val applicationId: Snowflake = applicationId
    override val token: String = token
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}
