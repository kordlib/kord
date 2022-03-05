package dev.kord.core.behavior.interaction.response

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.ActionInteractionBehavior
import dev.kord.core.behavior.interaction.InteractionBehavior
import dev.kord.core.entity.interaction.response.PublicMessageInteractionResponse
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * An [InteractionResponseBehavior] returned when using [ActionInteractionBehavior.deferPublicResponse].
 *
 * The main operation this handle supports is [respond][DeferredPublicMessageInteractionResponseBehavior.respond], the
 * user will see a 'loading' animation until it is invoked.
 *
 * The 'loading' animation is visible to all users in the [channel][InteractionBehavior.channel] the interaction was
 * sent from.
 *
 * This handle does not support sending followup messages to the interaction.
 */
public interface DeferredPublicMessageInteractionResponseBehavior :
    PublicInteractionResponseBehavior,
    DeferredMessageInteractionResponseBehavior {

    /**
     * Requests to delete the response.
     *
     * The 'loading' animation will stop and any attempt to call
     * [respond][DeferredPublicMessageInteractionResponseBehavior.respond] hereafter will fail.
     *
     * Returns a [FollowupPermittingInteractionResponseBehavior] that can still be used to send followup messages to the
     * interaction.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun delete(): FollowupPermittingInteractionResponseBehavior {
        kord.rest.interaction.deleteOriginalInteractionResponse(applicationId, token)
        return FollowupPermittingInteractionResponseBehavior(applicationId, token, kord)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DeferredPublicMessageInteractionResponseBehavior =
        DeferredPublicMessageInteractionResponseBehavior(applicationId, token, kord, strategy.supply(kord))
}

public fun DeferredPublicMessageInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): DeferredPublicMessageInteractionResponseBehavior = object : DeferredPublicMessageInteractionResponseBehavior {
    override val applicationId: Snowflake = applicationId
    override val token: String = token
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}

/**
 * Sends a public response message that was previously deferred by using
 * [ActionInteractionBehavior.deferPublicResponse].
 *
 * The response message is visible to all users in the [channel][InteractionBehavior.channel] the interaction was sent
 * from.
 *
 * This function is supposed to be only invoked once, use the returned [PublicMessageInteractionResponse] for more
 * operations.
 *
 * @param builder [InteractionResponseModifyBuilder] used to create the public response message.
 * @return [PublicMessageInteractionResponse] the public response message to the interaction.
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun DeferredPublicMessageInteractionResponseBehavior.respond(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): PublicMessageInteractionResponse {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    return editPublicOriginalResponse(builder)
}
