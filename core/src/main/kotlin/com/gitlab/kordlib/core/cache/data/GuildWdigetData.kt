package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordGuildWidget
import com.gitlab.kordlib.common.entity.Snowflake
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
