package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordGuildWidget
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
public data class GuildWidgetData(
    val enabled: Boolean,
    val channelId: Snowflake?
) {
    public companion object {
        public fun from(entity: DiscordGuildWidget): GuildWidgetData = with(entity) {
            GuildWidgetData(enabled, channelId)
        }
    }
}
