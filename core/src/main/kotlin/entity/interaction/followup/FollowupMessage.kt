package dev.kord.core.entity.interaction.followup

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.followup.FollowupMessageBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel

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




