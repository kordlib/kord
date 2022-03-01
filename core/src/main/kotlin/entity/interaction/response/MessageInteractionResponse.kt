package dev.kord.core.entity.interaction.response

import dev.kord.core.behavior.interaction.response.MessageInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * A [MessageInteractionResponseBehavior] that holds the response [message].
 *
 * @param message The response message. To use the message behavior your application must be authorized as a bot.
 */
public sealed class MessageInteractionResponse(public val message: Message) : MessageInteractionResponseBehavior {

    abstract override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageInteractionResponse
}
