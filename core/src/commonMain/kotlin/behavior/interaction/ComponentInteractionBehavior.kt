package dev.kord.core.behavior.interaction

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.entity.interaction.ComponentInteraction
import dev.kord.core.entity.interaction.ModalSubmitInteraction
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.message.create.UpdateMessageInteractionResponseCreateBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [ComponentInteraction] or a [ModalSubmitInteraction] that contains a
 * [message][ModalSubmitInteraction.message].
 */
public interface ComponentInteractionBehavior : ActionInteractionBehavior {
// this can not be a ModalParentInteractionBehavior since ModalSubmitInteractions implement ComponentInteractionBehavior
// but can not be responded to with another modal

    /**
     * Acknowledges the interaction with the intent of updating the original public message later by calling
     * [edit][PublicMessageInteractionResponseBehavior.edit] on the returned object.
     *
     * There is no requirement to actually update the message later, calling this is sufficient to handle the
     * interaction and stops any 'loading' animations in the client.
     *
     * There is nothing that will prevent you from calling this for an [ephemeral][MessageFlag.Ephemeral] message but
     * subsequent operations on the returned [PublicMessageInteractionResponseBehavior] might fail.
     *
     * This is not available for [ModalSubmitInteraction]s that do not contain a
     * [message][ModalSubmitInteraction.message].
     */
    public suspend fun deferPublicMessageUpdate(): PublicMessageInteractionResponseBehavior {
        kord.rest.interaction.deferMessageUpdate(id, token)
        return PublicMessageInteractionResponseBehavior(applicationId, token, kord)
    }

    /**
     * Acknowledges the interaction with the intent of updating the original [ephemeral][MessageFlag.Ephemeral] message
     * later by calling [edit][EphemeralMessageInteractionResponseBehavior.edit] on the returned object.
     *
     * There is no requirement to actually update the message later, calling this is sufficient to handle the
     * interaction and stops any 'loading' animations in the client.
     *
     * There is nothing that will prevent you from calling this for a public message but subsequent operations on the
     * returned [EphemeralMessageInteractionResponseBehavior] might fail.
     *
     * This is not available for [ModalSubmitInteraction]s that do not contain a
     * [message][ModalSubmitInteraction.message].
     */
    public suspend fun deferEphemeralMessageUpdate(): EphemeralMessageInteractionResponseBehavior {
        kord.rest.interaction.deferMessageUpdate(id, token)
        return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ComponentInteractionBehavior =
        ComponentInteractionBehavior(id, channelId!!, token, applicationId, kord, strategy)
}

/**
 * Creates a [ComponentInteractionBehavior] with the given [id], [channelId], [token], [applicationId], [kord] and
 * [strategy].
 */
public fun ComponentInteractionBehavior(
    id: Snowflake,
    channelId: Snowflake,
    token: String,
    applicationId: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): ComponentInteractionBehavior = object : ComponentInteractionBehavior {
    override val id: Snowflake = id
    override val channelId: Snowflake = channelId
    override val token: String = token
    override val applicationId: Snowflake = applicationId
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)
}

/**
 * Responds to the interaction by updating the original public message.
 *
 * There is nothing that will prevent you from calling this for an [ephemeral][MessageFlag.Ephemeral] message but
 * subsequent operations on the returned [PublicMessageInteractionResponseBehavior] might fail.
 *
 * This is not available for [ModalSubmitInteraction]s that do not contain a [message][ModalSubmitInteraction.message].
 */
public suspend inline fun ComponentInteractionBehavior.updatePublicMessage(
    builder: UpdateMessageInteractionResponseCreateBuilder.() -> Unit,
): PublicMessageInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = UpdateMessageInteractionResponseCreateBuilder().apply(builder).toRequest()
    kord.rest.interaction.createInteractionResponse(id, token, request)

    return PublicMessageInteractionResponseBehavior(applicationId, token, kord)
}

/**
 * Responds to the interaction by updating the original [ephemeral][MessageFlag.Ephemeral] message.
 *
 * There is nothing that will prevent you from calling this for a public message but subsequent operations on the
 * returned [EphemeralMessageInteractionResponseBehavior] might fail.
 *
 * This is not available for [ModalSubmitInteraction]s that do not contain a [message][ModalSubmitInteraction.message].
 */
public suspend inline fun ComponentInteractionBehavior.updateEphemeralMessage(
    builder: UpdateMessageInteractionResponseCreateBuilder.() -> Unit,
): EphemeralMessageInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = UpdateMessageInteractionResponseCreateBuilder().apply(builder).toRequest()
    kord.rest.interaction.createInteractionResponse(id, token, request)

    return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
}
