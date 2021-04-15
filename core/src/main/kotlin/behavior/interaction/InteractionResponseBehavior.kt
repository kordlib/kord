package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.KordObject

/**
 * The behavior of a [Discord Interaction Response](https://discord.com/developers/docs/interactions/slash-commands#interaction-response)
 */
@KordPreview
interface InteractionResponseBehavior : KordObject {
    val applicationId: Snowflake
    val token: String

}

