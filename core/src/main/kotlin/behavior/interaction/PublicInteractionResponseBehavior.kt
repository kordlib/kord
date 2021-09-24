package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.rest.request.RestRequestException


/**
 * The behavior of a public [Discord Interaction Response](https://discord.com/developers/docs/interactions/slash-commands#interaction-response)
 * This response is visible to all users in the channel.
 */

interface PublicInteractionResponseBehavior : InteractionResponseBehavior {

    /**
     * Requests to delete this interaction response.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete() {
        kord.rest.interaction.deleteOriginalInteractionResponse(applicationId, token)
    }

}


fun PublicInteractionResponseBehavior(applicationId: Snowflake, token: String, kord: Kord) =
    object : PublicInteractionResponseBehavior {
        override val applicationId: Snowflake
            get() = applicationId

        override val token: String
            get() = token

        override val kord: Kord
            get() = kord
    }
