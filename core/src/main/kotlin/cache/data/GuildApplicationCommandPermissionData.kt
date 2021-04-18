package dev.kord.core.cache.data

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordGuildApplicationCommandPermission
import dev.kord.common.entity.Snowflake

@KordPreview
data class GuildApplicationCommandPermissionData(
    val id: Snowflake,
    val type: DiscordGuildApplicationCommandPermission.Type,
    val permission: Boolean
) {
    companion object {
        fun from(permission: DiscordGuildApplicationCommandPermission) = with(permission) {
            GuildApplicationCommandPermissionData(id, type, this.permission)
        }
    }
}