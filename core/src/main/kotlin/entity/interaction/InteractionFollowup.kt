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
 * and behaves on it through [InteractionFollowupBehavior]
 *
 * @param message The message created by this follow-up.
 * To use the message behavior your application must be authorized as a bot.
 * @param token The unique token used to follow-up the Interaction Response.
 * @param applicationId the application id of bot invoking the follow-up
 * @param kord The kord instance responsible for the follow-up
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

class PublicFollowupMessage(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord
) : InteractionFollowup(message), PublicFollowupMessageBehavior


class EphemeralFollowupMessage(
    message: Message,
    override val applicationId: Snowflake,
    override val token: String,
    override val kord: Kord
) : InteractionFollowup(message), EphemeralFollowupMessageBehavior
