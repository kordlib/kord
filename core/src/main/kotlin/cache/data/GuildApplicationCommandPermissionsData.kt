package dev.kord.core.cache.data

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordGuildApplicationCommandPermissions
import dev.kord.common.entity.Snowflake

data class GuildApplicationCommandPermissionsData(
    val id: Snowflake,
    val applicationId: Snowflake,
    val guildId: Snowflake,
    val permissions: List<GuildApplicationCommandPermissionData>
) {

    companion object {
        fun from(permissions: DiscordGuildApplicationCommandPermissions) = with(permissions) {
            GuildApplicationCommandPermissionsData(
                id,
                applicationId,
                guildId,
                this.permissions.map(GuildApplicationCommandPermissionData::from)
            )
        }
    }
}