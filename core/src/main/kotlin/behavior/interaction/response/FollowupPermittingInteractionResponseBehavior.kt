package dev.kord.core.behavior.interaction.response

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.InteractionBehavior
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.entity.interaction.followup.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.followup.FollowupMessage
import dev.kord.core.entity.interaction.followup.PublicFollowupMessage
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * An [InteractionResponseBehavior] that supports sending followup messages to the interaction by using
 * [createPublicFollowup] or [createEphemeralFollowup].
 */
public interface FollowupPermittingInteractionResponseBehavior : InteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): FollowupPermittingInteractionResponseBehavior =
        FollowupPermittingInteractionResponseBehavior(applicationId, token, kord, strategy.supply(kord))
}

public fun FollowupPermittingInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): FollowupPermittingInteractionResponseBehavior = object : FollowupPermittingInteractionResponseBehavior {
    override val applicationId: Snowflake = applicationId
    override val token: String = token
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}

@Deprecated(
    "Renamed to 'createPublicFollowup'.",
    ReplaceWith(
        "this.createPublicFollowup { builder() }",
        "dev.kord.core.behavior.interaction.response.createPublicFollowup",
    ),
)
public suspend inline fun FollowupPermittingInteractionResponseBehavior.followUp(
    builder: FollowupMessageCreateBuilder.() -> Unit,
): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return createPublicFollowup(builder)
}

/**
 * Follows up an interaction response by sending a [FollowupMessage] without the
 * [Ephemeral flag][MessageFlag.Ephemeral].
 *
 * The response message is visible to all users in the [channel][InteractionBehavior.channel] the interaction was sent
 * from.
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun FollowupPermittingInteractionResponseBehavior.createPublicFollowup(
    builder: FollowupMessageCreateBuilder.() -> Unit,
): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.interaction.createFollowupMessage(applicationId, token, ephemeral = false, builder)
    val message = Message(response.toData(), kord)

    return PublicFollowupMessage(message, applicationId, token, kord)
}

@Deprecated(
    "Renamed to 'createEphemeralFollowup'.",
    ReplaceWith(
        "this.createEphemeralFollowup { builder() }",
        "dev.kord.core.behavior.interaction.response.createEphemeralFollowup",
    ),
)
public suspend inline fun FollowupPermittingInteractionResponseBehavior.followUpEphemeral(
    builder: FollowupMessageCreateBuilder.() -> Unit,
): EphemeralFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return createEphemeralFollowup(builder)
}

/**
 * Follows up an interaction response by sending a [FollowupMessage] with the [Ephemeral flag][MessageFlag.Ephemeral].
 *
 * The followup message is only visible to the [user][Interaction.user] who invoked the interaction.
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun FollowupPermittingInteractionResponseBehavior.createEphemeralFollowup(
    builder: FollowupMessageCreateBuilder.() -> Unit,
): EphemeralFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.interaction.createFollowupMessage(applicationId, token, ephemeral = true, builder)
    val message = Message(response.toData(), kord)

    return EphemeralFollowupMessage(message, applicationId, token, kord)
}
