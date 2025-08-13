package dev.kord.core.entity

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.GameActivityData

public class GameActivity(
    public val data: GameActivityData,
    override val kord: Kord
) : KordObject {
    public val activityLevel: Int = data.activityLevel

    public val activityScore: Int = data.activityScore
}