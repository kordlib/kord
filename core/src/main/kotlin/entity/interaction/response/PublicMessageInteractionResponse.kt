package dev.kord.core.entity.interaction.response

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * A [PublicMessageInteractionResponseBehavior] that holds the [message] this is a handle to.
 *
 * @param message The message. To use the message behavior your application must be authorized as a bot.
 */
public class PublicMessageInteractionResponse(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : MessageInteractionResponse(message), PublicMessageInteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicMessageInteractionResponse =
        PublicMessageInteractionResponse(message, applicationId, token, kord, strategy.supply(kord))
}
