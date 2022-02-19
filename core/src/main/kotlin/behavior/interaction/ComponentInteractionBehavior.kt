package dev.kord.core.behavior.interaction

import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.entity.interaction.ComponentInteraction
import dev.kord.core.entity.interaction.ModalSubmitInteraction
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.message.create.UpdateMessageInteractionResponseCreateBuilder
import dev.kord.rest.json.request.InteractionApplicationCommandCallbackData
import dev.kord.rest.json.request.InteractionResponseCreateRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [ComponentInteraction] or a [ModalSubmitInteraction] that contains a
 * [message][ModalSubmitInteraction.message].
 */
public interface ComponentInteractionBehavior : ActionInteractionBehavior {
// this can not be a ModalParentInteractionBehavior since ModalSubmitInteractions implement ComponentInteractionBehavior
// but can not be responded to with another modal (yet?)

    /**
     * Acknowledges a component interaction publicly with the intent of updating it later.
     *
     * There is no requirement to actually update the message later, calling this is
     * sufficient to handle the interaction and stops any 'loading' animations in the client.
     *
     * There is no noticeable difference between this and [acknowledgeEphemeralDeferredMessageUpdate]
     * when it comes to acknowledging the interaction, both functions can be called
     * on public and ephemeral messages.
     */
    @Deprecated(
        "Renamed to 'deferPublicMessageUpdate'. Also take a look at the new documentation.",
        ReplaceWith("this.deferPublicMessageUpdate()"),
    )
    public suspend fun acknowledgePublicDeferredMessageUpdate(): PublicMessageInteractionResponseBehavior {
        val request = InteractionResponseCreateRequest(
            type = InteractionResponseType.DeferredUpdateMessage
        )

        kord.rest.interaction.createInteractionResponse(id, token, request)

        return PublicMessageInteractionResponseBehavior(applicationId, token, kord)
    }

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
     * Acknowledges a component interaction ephemerally with the intent of updating it later.
     *
     * There is no requirement to actually update the message later, calling this is
     * sufficient to handle the interaction and stops any 'loading' state in the client.
     *
     * There is no noticeable difference between this and [acknowledgePublicDeferredMessageUpdate]
     * when it comes to acknowledging the interaction, both functions can be called
     * on public and ephemeral messages.
     */
    @Deprecated(
        "Renamed to 'deferEphemeralMessageUpdate'. Also take a look at the new documentation.",
        ReplaceWith("this.deferEphemeralMessageUpdate()"),
    )
    public suspend fun acknowledgeEphemeralDeferredMessageUpdate(): EphemeralMessageInteractionResponseBehavior {
        val request = InteractionResponseCreateRequest(
            data = Optional.Value(
                InteractionApplicationCommandCallbackData(
                    flags = Optional(MessageFlags(MessageFlag.Ephemeral))
                )
            ),
            type = InteractionResponseType.DeferredUpdateMessage
        )

        kord.rest.interaction.createInteractionResponse(id, token, request)

        return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
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
        ComponentInteractionBehavior(id, channelId, token, applicationId, kord, strategy)
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
 * Acknowledges a component interaction publicly and updates the message with the [builder].
 *
 * There is no noticeable difference between this and [acknowledgeEphemeralUpdateMessage]
 * when it comes to acknowledging the interaction, both functions can be called
 * on public and ephemeral messages.
 */
@Deprecated(
    "Renamed to 'updatePublicMessage'. Also take a look at the new documentation.",
    ReplaceWith("this.updatePublicMessage()", "dev.kord.core.behavior.interaction.updatePublicMessage"),
)
public suspend fun ComponentInteractionBehavior.acknowledgePublicUpdateMessage(
    builder: UpdateMessageInteractionResponseCreateBuilder.() -> Unit
): PublicMessageInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = UpdateMessageInteractionResponseCreateBuilder().apply(builder).toRequest()

    kord.rest.interaction.createInteractionResponse(
        id,
        token,
        request.copy(request = request.request.copy(InteractionResponseType.UpdateMessage))
    )

    return PublicMessageInteractionResponseBehavior(applicationId, token, kord)
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
 * Acknowledges a component interaction ephemerally and updates the message with the [builder].
 *
 * There is no noticeable difference between this and [acknowledgeEphemeralUpdateMessage]
 * when it comes to acknowledging the interaction, both functions can be called
 * on public and ephemeral messages.
 */
@Deprecated(
    "Renamed to 'updateEphemeralMessage'. Also take a look at the new documentation.",
    ReplaceWith("this.updateEphemeralMessage()", "dev.kord.core.behavior.interaction.updateEphemeralMessage"),
)
public suspend fun ComponentInteractionBehavior.acknowledgeEphemeralUpdateMessage(
    builder: UpdateMessageInteractionResponseCreateBuilder.() -> Unit
): EphemeralMessageInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = UpdateMessageInteractionResponseCreateBuilder().apply(builder).toRequest()

    kord.rest.interaction.createInteractionResponse(
        id,
        token,
        request
    )

    return EphemeralMessageInteractionResponseBehavior(applicationId, token, kord)
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
