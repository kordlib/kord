package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordGuildWidget
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class GuildWidgetData(
        val enabled: Boolean,
        val channelId: Snowflake?
) {
    companion object {
        fun from(entity: DiscordGuildWidget) : GuildWidgetData = with(entity){
            GuildWidgetData(enabled, channelId)
        }
    }
}
