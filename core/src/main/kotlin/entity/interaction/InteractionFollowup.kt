package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.EphemeralFollowupMessageBehavior
import dev.kord.core.behavior.interaction.FollowupMessageBehavior
import dev.kord.core.behavior.interaction.PublicFollowupMessageBehavior
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.MessageChannel

/**
 * Holds the follow-up [Message] resulting from an interaction follow-up
 * and behaves on it through [FollowupMessageBehavior]
 *
 * @param message The message created by this follow-up.
 * To use the message behavior your application must be authorized as a bot.
 */
@KordPreview
sealed class InteractionFollowup(val message: Message) : FollowupMessageBehavior {

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
class PublicFollowupMessage(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord
) : InteractionFollowup(message), PublicFollowupMessageBehavior


/**
 * Holds the follow-up [Message] resulting from an ephemeral followup message
 * and behaves on it through [EphemeralFollowupMessageBehavior].
 *
 * @param message The message created by this follow-up.
 * To use the message behavior your application must be authorized as a bot.
 * Note: Any rest calls made through the [message] object e.g: `message.delete()` will throw since the message
 * is deleted once the client receives it.
 */
class EphemeralFollowupMessage(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord
) : InteractionFollowup(message), EphemeralFollowupMessageBehavior
