package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.rest.request.RestRequestException


/**
 * The behavior of a public [Discord MessageRespondingInteraction Response](https://discord.com/developers/docs/interactions/slash-commands#interaction-response)
 * This response is visible to all users in the channel.
 */

public interface PublicInteractionResponseBehavior : InteractionResponseBehavior {

    /**
     * Requests to delete this interaction response.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete() {
        kord.rest.interaction.deleteOriginalInteractionResponse(applicationId, token)
    }

}


public fun PublicInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord
): PublicInteractionResponseBehavior =
    object : PublicInteractionResponseBehavior {
        override val applicationId: Snowflake
            get() = applicationId

        override val token: String
            get() = token

        override val kord: Kord
            get() = kord
    }
