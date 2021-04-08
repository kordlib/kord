package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.KordObject

@KordPreview
interface InteractionResponseBehavior : KordObject {
    val applicationId: Snowflake
    val token: String

}

