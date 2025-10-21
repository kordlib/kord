package dev.kord.core.cache.data

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordGameActivity
import kotlinx.serialization.Serializable

@KordPreview
@Serializable
public data class GameActivityData(
    val activityLevel: Int,
    val activityScore: Int
) {
    public companion object {
        public fun from(entity: DiscordGameActivity): GameActivityData = with(entity) {
            GameActivityData(
                activityLevel = activityLevel,
                activityScore = activityScore
            )
        }
    }
}

@KordPreview
public fun DiscordGameActivity.toData(): GameActivityData = GameActivityData.from(this)
