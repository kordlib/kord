package dev.kord.core.behavior.interaction

import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.interaction.ModalBuilder
import dev.kord.rest.builder.message.create.UpdateMessageInteractionResponseCreateBuilder
import dev.kord.rest.json.request.InteractionApplicationCommandCallbackData
import dev.kord.rest.json.request.InteractionResponseCreateRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface ComponentInteractionBehavior : ActionInteractionBehavior {

    /**
     * Acknowledges a component interaction publicly with the intent of updating it later.
     *
     * There is no requirement to actually update the message later, calling this is
     * sufficient to handle the interaction and stops any 'loading' animations in the client.
     *
     * There is no noticeable difference between this and [acknowledgeEphemeralDeferredMessageUpdate]
     * when it comes to acknowledging the interaction, both functions can be called
     * on public and ephemeral messages. The only difference is in the **followUp** calls,
     * which will become public or ephemeral respectively.
     */
    public suspend fun acknowledgePublicDeferredMessageUpdate(): PublicInteractionResponseBehavior {
        val request = InteractionResponseCreateRequest(
            type = InteractionResponseType.DeferredUpdateMessage
        )

        kord.rest.interaction.createInteractionResponse(id, token, request)

        return PublicInteractionResponseBehavior(applicationId, token, kord)
    }

    /**
     * Acknowledges a component interaction ephemerally with the intent of updating it later.
     *
     * There is no requirement to actually update the message later, calling this is
     * sufficient to handle the interaction and stops any 'loading' state in the client.
     *
     * There is no noticeable difference between this and [acknowledgePublicDeferredMessageUpdate]
     * when it comes to acknowledging the interaction, both functions can be called
     * on public and ephemeral messages. The only difference is in the **followUp** calls,
     * which will become ephemeral or public respectively.
     */
    public suspend fun acknowledgeEphemeralDeferredMessageUpdate(): EphemeralInteractionResponseBehavior {
        val request = InteractionResponseCreateRequest(
            data = Optional.Value(
                InteractionApplicationCommandCallbackData(
                    flags = Optional(MessageFlags(MessageFlag.Ephemeral))
                )
            ),
            type = InteractionResponseType.DeferredUpdateMessage
        )

        kord.rest.interaction.createInteractionResponse(id, token, request)

        return EphemeralInteractionResponseBehavior(applicationId, token, kord)
    }


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ComponentInteractionBehavior {
        return ComponentInteractionBehavior(id, channelId, token, applicationId, kord, strategy)
    }

}

public inline suspend fun ComponentInteractionBehavior.modal(title: String, customId: String, builder: ModalBuilder.() -> Unit): PublicInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    kord.rest.interaction.createModalInteractionResponse(id, token,title, customId, builder)
    return PublicInteractionResponseBehavior(applicationId, token, kord)
}

/**
 * Creates a ComponentInteractionBehavior with the given [id], [channelId],
 * [token], [applicationId], [kord] and [strategy].
 */

public fun ComponentInteractionBehavior(
    id: Snowflake,
    channelId: Snowflake,
    token: String,
    applicationId: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): ComponentInteractionBehavior = object : ComponentInteractionBehavior {
    override val applicationId: Snowflake
        get() = applicationId

    override val token: String
        get() = token

    override val channelId: Snowflake
        get() = channelId

    override val kord: Kord
        get() = kord

    override val id: Snowflake
        get() = id

    override val supplier: EntitySupplier = strategy.supply(kord)

}

/**
 * Acknowledges a component interaction publicly and updates the message with the [builder].
 *
 * There is no noticeable difference between this and [acknowledgeEphemeralUpdateMessage]
 * when it comes to acknowledging the interaction, both functions can be called
 * on public and ephemeral messages. The only difference is in the **followUp** calls,
 * which will become public or ephemeral respectively.
 */
public suspend fun ComponentInteractionBehavior.acknowledgePublicUpdateMessage(
    builder: UpdateMessageInteractionResponseCreateBuilder.() -> Unit
): PublicInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = UpdateMessageInteractionResponseCreateBuilder().apply(builder).toRequest()

    kord.rest.interaction.createInteractionResponse(
        id,
        token,
        request.copy(request = request.request.copy(InteractionResponseType.UpdateMessage))
    )

    return PublicInteractionResponseBehavior(applicationId, token, kord)
}

/**
 * Acknowledges a component interaction ephemerally and updates the message with the [builder].
 *
 * There is no noticeable difference between this and [acknowledgeEphemeralUpdateMessage]
 * when it comes to acknowledging the interaction, both functions can be called
 * on public and ephemeral messages. The only difference is in the **followUp** calls,
 * which will become ephemeral or public respectively.
 */
public suspend fun ComponentInteractionBehavior.acknowledgeEphemeralUpdateMessage(
    builder: UpdateMessageInteractionResponseCreateBuilder.() -> Unit
): EphemeralInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = UpdateMessageInteractionResponseCreateBuilder().apply(builder).toRequest()

    kord.rest.interaction.createInteractionResponse(
        id,
        token,
        request
    )

    return EphemeralInteractionResponseBehavior(applicationId, token, kord)
}
