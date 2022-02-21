package dev.kord.core.entity.interaction.followup

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.followup.PublicFollowupMessageBehavior
import dev.kord.core.entity.Message
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * Holds the followup [Message] resulting from a public followup message
 * and behaves on it through [PublicFollowupMessageBehavior]
 *
 * @param message The message created by this followup.
 * To use the message behavior your application must be authorized as a bot.
 */
public class PublicFollowupMessage(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : FollowupMessage(message), PublicFollowupMessageBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicFollowupMessage {
        return PublicFollowupMessage(message, applicationId, token, kord, strategy.supply(kord))
    }
}