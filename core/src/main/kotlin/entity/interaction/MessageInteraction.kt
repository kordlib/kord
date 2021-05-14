package dev.kord.core.entity.interaction

import cache.data.MessageInteractionData
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.User
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An instance of [MessageInteraction](https://discord.com/developers/docs/interactions/slash-commands#messageinteraction)
 * This is sent on the [Message] object when the message is a response to an [Interaction].
 */
@KordPreview
class MessageInteraction(
    val data: MessageInteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordEntity, Strategizable {
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

    /**
     * Requests the [User] of this interaction message.
     *
     * @throws RequestException if something went wrong while retrieving the user.
     * @throws EntityNotFoundException if the user was null.
     */
    suspend fun getUser(): User = supplier.getUser(user.id)

    /**
     * Requests to get the user of this interaction message,
     * returns null if the [User] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getUserOrNull(): User? = supplier.getUserOrNull(user.id)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable {
        return MessageInteraction(data, kord, strategy.supply(kord))
    }
}