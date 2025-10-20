package dev.kord.core.entity

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.GameActivityData

public class GameActivity(
    public val data: GameActivityData,
    override val kord: Kord
) : KordObject {
    /**
     * The activity level of the guild in the game
     */
    public val activityLevel: Int get() = data.activityLevel

    /**
     * The activity score of the guild in the game
     */
    public val activityScore: Int get() = data.activityScore
}