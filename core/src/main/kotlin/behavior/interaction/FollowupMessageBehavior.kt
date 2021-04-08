package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity

interface FollowupMessageBehavior : KordEntity {
    val applicationId: Snowflake
    val token: String
    val channelId: Snowflake

}