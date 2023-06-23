package dev.kord.core.behavior.interaction.response

import dev.kord.common.entity.Snowflake
import dev.kord.core.KordObject
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.entity.interaction.followup.FollowupMessage
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.request.RestRequestException

/**
 * A handle for operations that can follow an
 * [Interaction Response](https://discord.com/developers/docs/interactions/receiving-and-responding#responding-to-an-interaction).
 */
public sealed interface InteractionResponseBehavior : KordObject, Strategizable {

    /**
     * Copied from the [Interaction] the response is for.
     * @see [Interaction.applicationId].
     */
    public val applicationId: Snowflake

    /**
     * Copied from the [Interaction] the response is for.
     * @see [Interaction.token].
     */
    public val token: String

    /**
     * Returns a followup message for an interaction response or `null` if it was not found.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun getFollowupMessageOrNull(messageId: Snowflake): FollowupMessage? =
        supplier.getFollowupMessageOrNull(applicationId, token, messageId)

    /**
     * Returns a followup message for an interaction response.
     *
     * @throws RestRequestException if something went wrong during the request.
     * @throws EntityNotFoundException if the followup message was not found.
     */
    public suspend fun getFollowupMessage(messageId: Snowflake): FollowupMessage =
        supplier.getFollowupMessage(applicationId, token, messageId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InteractionResponseBehavior
}
