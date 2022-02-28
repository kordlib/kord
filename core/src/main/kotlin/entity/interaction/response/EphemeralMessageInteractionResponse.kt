package dev.kord.core.entity.interaction.response

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class EphemeralMessageInteractionResponse(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : MessageInteractionResponse(message), EphemeralMessageInteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralMessageInteractionResponse =
        EphemeralMessageInteractionResponse(message, applicationId, token, kord, strategy.supply(kord))
}
