package dev.kord.core.behavior.interaction.response

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.ActionInteractionBehavior
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.entity.interaction.response.EphemeralMessageInteractionResponse
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * An [InteractionResponseBehavior] returned when using [ActionInteractionBehavior.deferEphemeralResponse].
 *
 * The main operation this handle supports is [respond][DeferredEphemeralMessageInteractionResponseBehavior.respond],
 * the user will see a 'loading' animation until it is invoked.
 *
 * The 'loading' animation is only visible to the [user][Interaction.user] who invoked the interaction.
 *
 * This handle does not support sending followup messages to the interaction.
 */
public interface DeferredEphemeralMessageInteractionResponseBehavior :
    EphemeralInteractionResponseBehavior,
    DeferredMessageInteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DeferredEphemeralMessageInteractionResponseBehavior =
        DeferredEphemeralMessageInteractionResponseBehavior(applicationId, token, kord, strategy.supply(kord))
}

public fun DeferredEphemeralMessageInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): DeferredEphemeralMessageInteractionResponseBehavior = object : DeferredEphemeralMessageInteractionResponseBehavior {
    override val applicationId: Snowflake = applicationId
    override val token: String = token
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}

/**
 * Sends an [ephemeral][MessageFlag.Ephemeral] response message that was previously deferred by using
 * [ActionInteractionBehavior.deferEphemeralResponse].
 *
 * The response message is only visible to the [user][Interaction.user] who invoked the interaction.
 *
 * This function is supposed to be only invoked once, use the returned [EphemeralMessageInteractionResponse] for more
 * operations.
 *
 * @param builder [InteractionResponseModifyBuilder] used to create the ephemeral response message.
 * @return [EphemeralMessageInteractionResponse] the ephemeral response message to the interaction.
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun DeferredEphemeralMessageInteractionResponseBehavior.respond(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): EphemeralMessageInteractionResponse {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    return editEphemeralOriginalResponse(builder)
}
