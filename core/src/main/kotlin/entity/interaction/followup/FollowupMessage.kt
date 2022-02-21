package dev.kord.core.entity.interaction.followup

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.followup.FollowupMessageBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
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

    abstract override fun withStrategy(strategy: EntitySupplyStrategy<*>): FollowupMessage
}


@PublishedApi
internal fun FollowupMessage(message: Message, applicationId: Snowflake, token: String, kord: Kord): FollowupMessage {
    val isEphemeral = message.flags?.contains(MessageFlag.Ephemeral) ?: false
    return when {
        isEphemeral -> EphemeralFollowupMessage(message, applicationId, token, kord)
        else -> PublicFollowupMessage(message, applicationId, token, kord)
    }
}
