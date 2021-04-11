package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity

@KordPreview
interface FollowupMessageBehavior : KordEntity {

    val applicationId: Snowflake
    val token: String
    val channelId: Snowflake

}