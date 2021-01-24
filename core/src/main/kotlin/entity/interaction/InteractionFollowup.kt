package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.InteractionFollowupBehavior
import dev.kord.core.cache.data.MessageData
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
class InteractionFollowup(
     val message: Message,
    override val token: String,
    override val applicationId: Snowflake,
    override val kord: Kord,
) : InteractionFollowupBehavior {

    /**
     * The id of the follow-up message.
     */
    override val id: Snowflake = message.id

    /**
     * The id of the [MessageChannel] the follow-up message was send in.
     */
    override val channelId: Snowflake = message.channelId

}
