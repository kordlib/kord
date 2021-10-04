package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplier

/**
 * The behavior of a [Discord Interaction](https://discord.com/developers/docs/interactions/slash-commands#interaction)
 * with [Application Command type][dev.kord.common.entity.ApplicationCommandType]
 */

public interface ApplicationCommandInteractionBehavior : InteractionBehavior

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
