package dev.kord.core.entity.application

import dev.kord.common.entity.ApplicationCommandPermissionType
import dev.kord.common.entity.DiscordGuildApplicationCommandPermission
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.GuildApplicationCommandPermissionData
import dev.kord.core.cache.data.GuildApplicationCommandPermissionsData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.DeprecationLevel.HIDDEN

public class GuildApplicationCommandPermission(public val data: GuildApplicationCommandPermissionData) {

    public val id: Snowflake get() = data.id

    public val type: ApplicationCommandPermissionType get() = data.type

    @Suppress("DEPRECATION_ERROR")
    @Deprecated("Binary compatibility", level = HIDDEN)
    @get:JvmName("getType")
    public val type0: DiscordGuildApplicationCommandPermission.Type
        get() = when (val t = type) {
            ApplicationCommandPermissionType.Role -> DiscordGuildApplicationCommandPermission.Type.Role
            ApplicationCommandPermissionType.User -> DiscordGuildApplicationCommandPermission.Type.User
            ApplicationCommandPermissionType.Channel -> DiscordGuildApplicationCommandPermission.Type.Channel
            is ApplicationCommandPermissionType.Unknown -> DiscordGuildApplicationCommandPermission.Type.Unknown(t.value)
        }

    public val permission: Boolean get() = data.permission
}


public class ApplicationCommandPermissions(public val data: GuildApplicationCommandPermissionsData) {
    public val id: Snowflake get() = data.id

    public val applicationId: Snowflake get() = data.applicationId

    public val guildId: Snowflake get() = data.guildId

    public val permissions: Flow<GuildApplicationCommandPermission>
        get() = flow {
            data.permissions.forEach { emit(GuildApplicationCommandPermission(it)) }
        }
}
