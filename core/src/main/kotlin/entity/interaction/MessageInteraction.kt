package dev.kord.core.entity.interaction

import cache.data.MessageInteractionData
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Message

/**
 * An instance of [MessageInteraction](https://discord.com/developers/docs/interactions/slash-commands#messageinteraction)
 * This is sent on the [Message] object when the message is a response to an [Interaction].
 */
@KordPreview
class MessageInteraction(
    val data: MessageInteractionData,
    override val kord: Kord,
) : KordEntity {
    /**
     * [id][Interaction.id] of the [Interaction] this message is responding to.
     */
    override val id: Snowflake get() = data.id

    /**
     * 	the [name][ApplicationCommand.name] of the [ApplicationCommand] that triggered this message.
     */
    val name: String get() = data.name

    /**
     * The [UserBehavior] of the [user][Interaction.user] who invoked the [Interaction]
     */
    val user: UserBehavior get() = UserBehavior(data.id, kord)

    /**
     * the [InteractionType] of the interaction [MessageInteraction].
     */
    val type: InteractionType get() = data.type
}