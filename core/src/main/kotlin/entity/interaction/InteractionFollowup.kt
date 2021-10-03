package dev.kord.core.entity.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.EphemeralFollowupMessageBehavior
import dev.kord.core.behavior.interaction.FollowupMessageBehavior
import dev.kord.core.behavior.interaction.PublicFollowupMessageBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * Holds the follow-up [Message] resulting from an interaction follow-up
 * and behaves on it through [FollowupMessageBehavior]
 *
 * @param message The message created by this follow-up.
 * To use the message behavior your application must be authorized as a bot.
 */

public sealed class InteractionFollowup(public val message: Message) : FollowupMessageBehavior {

    /**
     * The id of the follow-up message.
     */
    override val id: Snowflake get() = message.id

    /**
     * The id of the [MessageChannel] the follow-up message was send in.
     */
    override val channelId: Snowflake get() = message.channelId


}


/**
 * Holds the follow-up [Message] resulting from an public followup message
 * and behaves on it through [PublicFollowupMessageBehavior]
 *
 * @param message The message created by this follow-up.
 * To use the message behavior your application must be authorized as a bot.
 */

public class PublicFollowupMessage(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : InteractionFollowup(message), PublicFollowupMessageBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicFollowupMessage {
        return PublicFollowupMessage(message, applicationId, token, kord, strategy.supply(kord))
    }
}


/**
 * Holds the follow-up [Message] resulting from an ephemeral followup message
 * and behaves on it through [EphemeralFollowupMessageBehavior].
 *
 * @param message The message created by this follow-up.
 * To use the message behavior your application must be authorized as a bot.
 * Note: Any rest calls made through the [message] object e.g: `message.delete()` will throw since the message
 * is deleted once the client receives it.
 */

public class EphemeralFollowupMessage(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : InteractionFollowup(message), EphemeralFollowupMessageBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralFollowupMessage {
        return EphemeralFollowupMessage(message, applicationId, token, kord, strategy.supply(kord))
    }
}
