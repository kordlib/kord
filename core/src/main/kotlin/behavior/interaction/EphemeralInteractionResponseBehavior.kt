package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord

/**
 * The behavior of a ephemeral [Discord Interaction Response](https://discord.com/developers/docs/interactions/slash-commands#interaction-response)
 * This response is visible to *only* to the user who made the interaction.
 */

interface EphemeralInteractionResponseBehavior : InteractionResponseBehavior


fun EphemeralInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord
): EphemeralInteractionResponseBehavior =
    object : EphemeralInteractionResponseBehavior {
        override val applicationId: Snowflake
            get() = applicationId

        override val token: String
            get() = token

        override val kord: Kord
            get() = kord
    }
