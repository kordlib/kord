package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordStageInstance
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class StageInstanceData(
    val id: Snowflake,
    val guildId: Snowflake,
    val channelId: Snowflake,
    val topic: String
) {
    companion object {
        fun from(stageInstance: DiscordStageInstance) = with(stageInstance) {
            StageInstanceData(id, guildId, channelId, topic)
        }
    }
}

fun DiscordStageInstance.toData(): StageInstanceData {
    return StageInstanceData.from(this)
}