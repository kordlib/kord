package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The behavior of a ephemeral [Discord ActionInteraction Response](https://discord.com/developers/docs/interactions/slash-commands#interaction-response)
 * This response is visible to *only* to the user who made the interaction.
 */

public interface EphemeralInteractionResponseBehavior : InteractionResponseBehavior


public fun EphemeralInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy,
): EphemeralInteractionResponseBehavior =
    object : EphemeralInteractionResponseBehavior {
        override val applicationId: Snowflake = applicationId

        override val token: String = token

        override val kord: Kord = kord

        override val supplier: EntitySupplier = strategy.supply(kord)

        override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralInteractionResponseBehavior =
            EphemeralInteractionResponseBehavior(applicationId, token, kord, strategy)
    }
