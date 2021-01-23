package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.InteractionFollowupBehavior
import dev.kord.core.cache.data.MessageData
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.MessageChannel

/**
 * A Message that's created to follow up an [Interaction Response][dev.kord.core.behavior.InteractionResponseBehavior]
 */
@KordPreview
class InteractionFollowup(
    val data: MessageData,
    override val token: String,
    override val applicationId: Snowflake,
    override val kord: Kord,
) : InteractionFollowupBehavior {

    /**
     * The id of the follow-up message.
     */
    override val id: Snowflake
        get() = data.id

    /**
     * The id of the [MessageChannel] the follow-up message was send in.
     */
    override val channelId: Snowflake
        get() = data.channelId

    /**
     * The message created by this follow-up.
     *
     * To use the message behavior your application must be authorized as a bot.
     */
    val message: Message
        get() = Message(data, kord)

}
