package dev.kord.core.cache.data

import dev.kord.common.entity.ApplicationCommandPermissionType
import dev.kord.common.entity.DiscordGuildApplicationCommandPermission
import dev.kord.common.entity.Snowflake

public data class GuildApplicationCommandPermissionData(
    val id: Snowflake,
    val type: ApplicationCommandPermissionType,
    val permission: Boolean
) {
    public companion object {
        public fun from(permission: DiscordGuildApplicationCommandPermission): GuildApplicationCommandPermissionData =
            with(permission) {
                GuildApplicationCommandPermissionData(id, type, this.permission)
            }
    }
}
