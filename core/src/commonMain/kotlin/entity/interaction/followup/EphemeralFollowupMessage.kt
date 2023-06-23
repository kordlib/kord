package dev.kord.core.entity.interaction.followup

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.followup.EphemeralFollowupMessageBehavior
import dev.kord.core.entity.Message
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * Holds the followup [Message] resulting from an ephemeral followup message
 * and behaves on it through [EphemeralFollowupMessageBehavior].
 *
 * @param message The message created by this followup. Any rest calls made through the message behavior, e.g.
 * `message.delete()`, will throw since ephemeral messages are not accessible through bot authorization.
 */
public class EphemeralFollowupMessage(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : FollowupMessage(message), EphemeralFollowupMessageBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralFollowupMessage {
        return EphemeralFollowupMessage(message, applicationId, token, kord, strategy.supply(kord))
    }
}
