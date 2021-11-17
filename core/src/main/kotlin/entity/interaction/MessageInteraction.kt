package dev.kord.core.entity.interaction

import cache.data.MessageInteractionData
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
 * This is sent on the [Message] object when the message is a response to an [MessageRespondingInteraction].
 */

public class MessageInteraction(
    public val data: MessageInteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordEntity, Strategizable {
    /**
     * [id][MessageRespondingInteraction.id] of the [MessageRespondingInteraction] this message is responding to.
     */
    override val id: Snowflake get() = data.id

    /**
     * the [name][ApplicationCommand.name] of the [ApplicationCommand] that triggered this message.
     */
    public val name: String get() = data.name

    /**
     * The [UserBehavior] of the [user][MessageRespondingInteraction.user] who invoked the [MessageRespondingInteraction]
     */
    public val user: UserBehavior get() = UserBehavior(data.user, kord)

    /**
     * the [InteractionType] of the interaction [MessageInteraction].
     */
    public val type: InteractionType get() = data.type

    /**
     * Requests the [User] of this interaction message.
     *
     * @throws RequestException if something went wrong while retrieving the user.
     * @throws EntityNotFoundException if the user was null.
     */
    public suspend fun getUser(): User = supplier.getUser(user.id)

    /**
     * Requests to get the user of this interaction message,
     * returns null if the [User] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(user.id)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageInteraction {
        return MessageInteraction(data, kord, strategy.supply(kord))
    }
}
