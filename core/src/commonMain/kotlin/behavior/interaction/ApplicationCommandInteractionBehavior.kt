package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.ApplicationCommandInteraction
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/** The behavior of an [ApplicationCommandInteraction]. */
public interface ApplicationCommandInteractionBehavior : ModalParentInteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ApplicationCommandInteractionBehavior =
        ApplicationCommandInteractionBehavior(id, channelId, token, applicationId, kord, supplier)
}

internal fun ApplicationCommandInteractionBehavior(
    id: Snowflake,
    channelId: Snowflake,
    token: String,
    applicationId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
) = object : ApplicationCommandInteractionBehavior {
    override val id: Snowflake = id
    override val channelId: Snowflake = channelId
    override val token: String = token
    override val applicationId: Snowflake = applicationId
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}
