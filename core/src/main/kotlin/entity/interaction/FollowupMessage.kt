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

@Deprecated(
    "'InteractionFollowup' was renamed to 'FollowupMessage'.",
    ReplaceWith("FollowupMessage", "dev.kord.core.entity.interaction.FollowupMessage"),
    DeprecationLevel.ERROR,
)
public typealias InteractionFollowup = FollowupMessage

/**
 * Holds the followup [Message] resulting from an interaction followup
 * and behaves on it through [FollowupMessageBehavior].
 *
 * @param message The message created by this followup.
 * To use the message behavior your application must be authorized as a bot.
 */
public sealed class FollowupMessage(public val message: Message) : FollowupMessageBehavior {

    /**
     * The id of the followup message.
     */
    override val id: Snowflake get() = message.id

    /**
     * The id of the [MessageChannel] the followup message was sent in.
     */
    override val channelId: Snowflake get() = message.channelId


}


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


/**
 * Holds the followup [Message] resulting from an ephemeral followup message
 * and behaves on it through [EphemeralFollowupMessageBehavior].
 *
 * @param message The message created by this followup.
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
) : FollowupMessage(message), EphemeralFollowupMessageBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralFollowupMessage {
        return EphemeralFollowupMessage(message, applicationId, token, kord, strategy.supply(kord))
    }
}
