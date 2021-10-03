package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordStageInstance
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
public data class StageInstanceData(
    val id: Snowflake,
    val guildId: Snowflake,
    val channelId: Snowflake,
    val topic: String
) {
    public companion object {
        public fun from(stageInstance: DiscordStageInstance): StageInstanceData = with(stageInstance) {
            StageInstanceData(id, guildId, channelId, topic)
        }
    }
}

public fun DiscordStageInstance.toData(): StageInstanceData {
    return StageInstanceData.from(this)
}
