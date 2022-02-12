package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.PopupInteractionResponseBehavior
import dev.kord.core.supplier.EntitySupplier
import dev.kord.rest.builder.interaction.ModalBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord ActionInteraction](https://discord.com/developers/docs/interactions/slash-commands#interaction)
 * with [Application Command type][dev.kord.common.entity.ApplicationCommandType]
 */
public interface ApplicationCommandInteractionBehavior : ActionInteractionBehavior

internal fun ApplicationCommandInteractionBehavior(
    id: Snowflake,
    channelId: Snowflake,
    token: String,
    applicationId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
) = object : ApplicationCommandInteractionBehavior {

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
    override val supplier: EntitySupplier
        get() = supplier

}

public inline suspend fun ApplicationCommandInteractionBehavior.modal(title: String, customId: String,builder: ModalBuilder.() -> Unit): PopupInteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
     kord.rest.interaction.createModalInteractionResponse(id, token,title, customId, builder)
    return PopupInteractionResponseBehavior(applicationId, token, kord)
}
