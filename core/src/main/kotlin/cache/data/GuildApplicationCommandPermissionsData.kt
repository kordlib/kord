package dev.kord.core.cache.data

import dev.kord.cache.api.data.description
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
        val description = description(GuildApplicationCommandPermissionsData::id) {
            link(GuildApplicationCommandPermissionsData::guildId to GuildData::id)
            link(GuildApplicationCommandPermissionsData::id to ApplicationCommandData::id)
        }
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