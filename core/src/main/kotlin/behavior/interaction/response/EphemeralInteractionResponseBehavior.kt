package dev.kord.core.behavior.interaction.response

/**
 * The behavior of an ephemeral [Discord ActionInteraction Response](https://discord.com/developers/docs/interactions/slash-commands#interaction-response)
 * This response is visible to *only* to the user who made the interaction.
 */
public sealed interface EphemeralInteractionResponseBehavior : InteractionResponseBehavior