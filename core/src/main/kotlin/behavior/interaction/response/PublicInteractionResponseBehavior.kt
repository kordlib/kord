package dev.kord.core.behavior.interaction.response

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.InteractionResponseBehavior
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.request.RestRequestException


/**
 * The behavior of a public [Discord ActionInteraction Response](https://discord.com/developers/docs/interactions/slash-commands#interaction-response)
 * This response is visible to all users in the channel.
 */

public sealed interface PublicInteractionResponseBehavior : InteractionResponseBehavior