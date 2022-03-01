package dev.kord.core.behavior.interaction

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.ActionInteraction
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.EntitySupplyStrategy.Companion.rest
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** The behavior of an [ActionInteraction]. */
public interface ActionInteractionBehavior : InteractionBehavior {

    /**
     * Acknowledges the interaction ephemerally. The user will see a 'loading' animation.
     *
     * Call [edit][EphemeralMessageInteractionResponseBehavior.edit] on the returned object to edit the response later.
     */
    @Deprecated("Renamed to 'deferEphemeralMessage'.", ReplaceWith("this.deferEphemeralMessage()"))
    public suspend fun acknowledgeEphemeral(): EphemeralMessageInteractionResponseBehavior = deferEphemeralMessage()

    /**
     * Acknowledges the interaction ephemerally with the intent of responding with an [ephemeral][MessageFlag.Ephemeral]
     * message later by calling [edit][EphemeralMessageInteractionResponseBehavior.edit] on the returned object. The
     * user will see a 'loading' animation.
     */
    public suspend fun deferEphemeralMessage(): EphemeralMessageInteractionResponseBehavior {
        kord.rest.interaction.deferMessage(id, token, ephemeral = true)
        return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
    }

    /**
     * Acknowledges the interaction publicly. The user will see a 'loading' animation.
     *
     * Call [edit][PublicMessageInteractionResponseBehavior.edit] on the returned object to edit the response later.
     */
    @Deprecated("Renamed to 'deferPublicMessage'.", ReplaceWith("this.deferPublicMessage()"))
    public suspend fun acknowledgePublic(): PublicMessageInteractionResponseBehavior = deferPublicMessage()

    /**
     * Acknowledges the interaction publicly with the intent of responding with a public message later by calling
     * [edit][PublicMessageInteractionResponseBehavior.edit] on the returned object. The user will see a 'loading'
     * animation.
     */
    public suspend fun deferPublicMessage(): PublicMessageInteractionResponseBehavior {
        kord.rest.interaction.deferMessage(id, token, ephemeral = false)
        return PublicMessageInteractionResponseBehavior(applicationId, token, kord)
    }

    /**
     * Returns the initial interaction response or `null` if it was not found.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun getOriginalInteractionResponseOrNull(): Message? =
        kord.with(rest).getOriginalInteractionOrNull(applicationId, token)

    /**
     * Returns the initial interaction response.
     *
     * @throws RestRequestException if something went wrong during the request.
     * @throws EntityNotFoundException if the initial interaction response was not found.
     */
    public suspend fun getOriginalInteractionResponse(): Message =
        kord.with(rest).getOriginalInteraction(applicationId, token)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ActionInteractionBehavior =
        ActionInteractionBehavior(id, channelId, token, applicationId, kord, strategy)
}


/**
 * Responds to the interaction with a public message.
 *
 * @param builder [InteractionResponseCreateBuilder] used to create the public response.
 * @return [PublicMessageInteractionResponseBehavior] public response to the interaction.
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun ActionInteractionBehavior.respondPublic(
    builder: InteractionResponseCreateBuilder.() -> Unit
): PublicMessageInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    kord.rest.interaction.createInteractionResponse(id, token, ephemeral = false, builder)
    return PublicMessageInteractionResponseBehavior(applicationId, token, kord)
}


/**
 * Responds to the interaction with an [ephemeral][MessageFlag.Ephemeral] message.
 *
 * @param builder [InteractionResponseCreateBuilder] used to create the ephemeral response.
 * @return [EphemeralMessageInteractionResponseBehavior] ephemeral response to the interaction.
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun ActionInteractionBehavior.respondEphemeral(
    builder: InteractionResponseCreateBuilder.() -> Unit
): EphemeralMessageInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    kord.rest.interaction.createInteractionResponse(id, token, ephemeral = true, builder)
    return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
}

public fun ActionInteractionBehavior(
    id: Snowflake,
    channelId: Snowflake,
    token: String,
    applicationId: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): ActionInteractionBehavior = object : ActionInteractionBehavior {
    override val id: Snowflake = id
    override val channelId: Snowflake = channelId
    override val token: String = token
    override val applicationId: Snowflake = applicationId
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)
}
