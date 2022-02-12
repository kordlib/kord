package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.EphemeralInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.PublicInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.entity.Message
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.EntitySupplyStrategy.Companion.rest
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord ActionInteraction](https://discord.com/developers/docs/interactions/slash-commands#interaction) which does perform an action
 * (e.g. slash commands and context actions)
 */
public interface ActionInteractionBehavior : InteractionBehavior {

    /**
     * Acknowledges an interaction ephemerally.
     *
     * @return [EphemeralInteractionResponseBehavior] Ephemeral acknowledgement of the interaction.
     */
    public suspend fun acknowledgeEphemeral(): EphemeralMessageInteractionResponseBehavior {
        kord.rest.interaction.acknowledge(id, token, ephemeral = true)
        return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
    }

    /**
     * Acknowledges an interaction.
     *
     * @return [PublicInteractionResponseBehavior] public acknowledgement of an interaction.
     */
    public suspend fun acknowledgePublic(): PublicInteractionResponseBehavior {
        kord.rest.interaction.acknowledge(id, token, ephemeral = false)
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
}


/**
 * Acknowledges an interaction and responds with [PublicInteractionResponseBehavior].
 *
 * @param builder [InteractionResponseCreateBuilder] used to create a public response.
 * @return [PublicInteractionResponseBehavior] public response to the interaction.
 */
public suspend inline fun ActionInteractionBehavior.respondPublic(
    builder: InteractionResponseCreateBuilder.() -> Unit
): PublicInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    kord.rest.interaction.createInteractionResponse(id, token, ephemeral = false, builder)
    return PublicMessageInteractionResponseBehavior(applicationId, token, kord)
}


/**
 * Acknowledges an interaction and responds with [EphemeralInteractionResponseBehavior] with ephemeral flag.
 *
 * @param builder [InteractionResponseCreateBuilder] used to a create an ephemeral response.
 * @return [InteractionResponseBehavior] ephemeral response to the interaction.
 */
public suspend inline fun ActionInteractionBehavior.respondEphemeral(
    builder: InteractionResponseCreateBuilder.() -> Unit
): EphemeralInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    kord.rest.interaction.createInteractionResponse(id, token, ephemeral = true, builder)
    return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
}

public fun InteractionBehavior(
    id: Snowflake,
    channelId: Snowflake,
    token: String,
    applicationId: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): ActionInteractionBehavior = object : ActionInteractionBehavior {
    override val id: Snowflake
        get() = id

    override val token: String
        get() = token

    override val applicationId: Snowflake
        get() = applicationId

    override val kord: Kord
        get() = kord

    override val channelId: Snowflake
        get() = channelId


    override val supplier: EntitySupplier = strategy.supply(kord)

}
